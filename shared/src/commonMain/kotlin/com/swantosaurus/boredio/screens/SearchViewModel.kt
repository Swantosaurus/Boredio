package com.swantosaurus.boredio.screens

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import com.swantosaurus.boredio.ViewModel
import com.swantosaurus.boredio.activity.dataSource.ActivityDataSource
import com.swantosaurus.boredio.activity.model.Activity
import com.swantosaurus.boredio.activity.model.ActivityType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val backgroundSingleThreadScope =
    CoroutineScope(newSingleThreadContext("SearchViewModelBackgroundScope"))

private val logger = Logger.withTag("SearchViewModel")

private const val INIT_LOAD_COUNT = 20
private const val NEXT_PAGE_LOAD = 10

private const val PREFERENCES_PREFIX = "search_view_model_"

private val typesKey = stringPreferencesKey("${PREFERENCES_PREFIX}types")
private val minParticipantsKey = intPreferencesKey("${PREFERENCES_PREFIX}minParticipants")
private val maxParticipantsKey = intPreferencesKey("${PREFERENCES_PREFIX}maxParticipants")
private val minPriceKey = doublePreferencesKey("${PREFERENCES_PREFIX}minPrice")
private val maxPriceKey = doublePreferencesKey("${PREFERENCES_PREFIX}maxPrice")
private val minAccessibilityKey = doublePreferencesKey("${PREFERENCES_PREFIX}minAccessibility")
private val maxAccessibilityKey = doublePreferencesKey("${PREFERENCES_PREFIX}maxAccessibility")


data class ActivitySearch(
    val activity: Activity, val isSaved: Boolean
)

class SearchViewModel(
    private val activityDataSource: ActivityDataSource,
    private val preferences: DataStore<Preferences>
) : ViewModel() {

    val currentParams = preferences.data.map { pref ->
        val types: List<ActivityType> =
            pref[typesKey]?.let { Json.decodeFromString(it) } ?: emptyList()
        val minParticipants = pref[minParticipantsKey] ?: 1
        val maxParticipants =
            pref[maxParticipantsKey]?.takeIf { it >= minParticipants } ?: (minParticipants + 10)
        val minPrice = pref[minPriceKey]?.coerceIn(0.0..1.0) ?: 0.0
        val maxPrice = pref[maxPriceKey]?.takeIf { it >= minPrice } ?: 1.0
        val minAccessibility = pref[minAccessibilityKey]?.coerceIn(0.0..1.0) ?: 0.0
        val maxAccessibility = pref[maxAccessibilityKey]?.takeIf { it >= minAccessibility } ?: 1.0

        CallParameters(
            types = types,
            minParticipants = minParticipants,
            maxParticipants = maxParticipants,
            minPrice = minPrice,
            maxPrice = maxPrice,
            minAccessibility = minAccessibility,
            maxAccessibility = maxAccessibility
        )
    }


    private val _searchState = MutableStateFlow<SearchState>(SearchState.Init)
    val searchState = _searchState.asStateFlow()

    private var _canLoadMore = MutableStateFlow<Boolean>(true)
    var canLoadMore = _canLoadMore.asStateFlow()


    fun reload() {
        if (searchState.value !is SearchState.Error) {
            logger.e { "reload called while not in error state - cancel" }
            return
        }

        backgroundSingleThreadScope.launch {
            searchActivity(
                (searchState.value as SearchState.Success).calledParameters
            )
        }
    }

    fun search() {
        backgroundSingleThreadScope.launch {
            currentParams.firstOrNull()?.let {
                searchActivity(
                    it
                )
            }
        }
    }

    fun changeParams(
        calledParameters: CallParameters
    ) {
        logger.d { "maxPrice ${calledParameters.maxPrice}" }
        backgroundSingleThreadScope.launch {
            preferences.edit {
                it[typesKey] = Json.encodeToString(calledParameters.types)
                it[minParticipantsKey] = calledParameters.minParticipants
                it[maxParticipantsKey] = calledParameters.maxParticipants
                it[minPriceKey] = calledParameters.minPrice
                it[maxPriceKey] = calledParameters.maxPrice
                it[minAccessibilityKey] = calledParameters.minAccessibility
                it[maxAccessibilityKey] = calledParameters.maxAccessibility
            }
        }
    }

    fun saveActivity(activity: Activity) {
        backgroundSingleThreadScope.launch {
            _searchState.update { state ->
                if (_searchState.value !is SearchState.Success) {
                    logger.e { "saveActivity called while not in success state - cancel" }
                    return@update state
                }
                val updatedActivity = activityDataSource.storeActivity(activity)
                SearchState.Success(
                    (state as SearchState.Success).activities.map {
                        if (it.key == activity.key) updatedActivity else it
                    }, state.calledParameters
                )
            }
        }
    }

    fun likeActivityToggle(activity: Activity) {
        backgroundSingleThreadScope.launch {
            _searchState.update { state ->
                if (_searchState.value !is SearchState.Success) {
                    logger.e { "likeActivity called while not in success state - cancel" }
                    return@update state
                }
                val updatedActivity =
                    activityDataSource.storeActivity(activity.copy(favorite = !activity.favorite))
                SearchState.Success(
                    (state as SearchState.Success).activities.map {
                        if (it.key == activity.key) updatedActivity else it
                    }, state.calledParameters
                )
            }
        }
    }


    @OptIn(InternalCoroutinesApi::class)
    fun loadMore() {
        if (searchState.value !is SearchState.Success) {
            logger.w { "loadMore called while not in success state - cancel" }
            return
        }

        //this has to be synchronized but didn't find a way to do it multiplatform safely so I used a workaround
        backgroundSingleThreadScope.launch {
                if (searchState.value !is SearchState.Success) {
                    logger.w { "loadMore called while not in success state - cancel" }
                    cancel()
                }
                if (!canLoadMore.value) return@launch
                _searchState.update {
                    SearchState.LoadingMore(
                        (searchState.value as SearchState.Success).activities,
                        (searchState.value as SearchState.Success).calledParameters
                    )
                }

                val activities = MutableList(NEXT_PAGE_LOAD) {
                    activityDataSource.getRandomByParameters(
                        (searchState.value as SearchState.LoadingMore).calledParameters
                    )
                }

            if (activities.filterNotNull().isEmpty()) {
                logger.w { "No new activity loaded" }
                _searchState.update {
                    SearchState.Success(
                        (searchState.value as SearchState.LoadingMore).activities,
                        (searchState.value as SearchState.LoadingMore).calledParameters
                    )
                }
                _canLoadMore.update { false }
                return@launch
            }
                //remove duplicates
            val oldActivities = (searchState.value as SearchState.LoadingMore).activities
            val allActivities = (oldActivities + activities).toMutableList()

            for (i in allActivities.indices) {
                val startJ = if (i < oldActivities.size) oldActivities.size else i + 1
                for (j in startJ until allActivities.size) {
                    if (allActivities[i]?.key == allActivities[j]?.key && allActivities[i]?.key != null) {
                            logger.w { "Duplicate activity found" }
                        allActivities[j] = null
                        }
                    }
            }

            _searchState.update {
                val filteredActivities = activities.filterNotNull()
                if (filteredActivities.size < NEXT_PAGE_LOAD * 2.0 / 3) {
                    logger.i { "Too much duplicities stopping loadMore" }
                    _canLoadMore.update { false }
                }
                SearchState.Success(
                    allActivities.filterNotNull(),
                    (searchState.value as SearchState.LoadingMore).calledParameters
                )
            }
        }
    }

    private suspend fun ActivityDataSource.getRandomByParameters(calledParameters: CallParameters) =
        getRandomByParameters(
            calledParameters.types,
            calledParameters.minParticipants,
            calledParameters.maxParticipants,
            calledParameters.minPrice,
            calledParameters.maxPrice,
            calledParameters.minAccessibility,
            calledParameters.maxAccessibility,
        )


    @OptIn(InternalCoroutinesApi::class)
    private suspend fun searchActivity(
        calledParameters: CallParameters
    ) {
        val (types, minParticipants, maxParticipants, minPrice, maxPrice, minAccessibility, maxAccessibility) = calledParameters
        if (searchState.value !is SearchState.Success && searchState.value !is SearchState.Init) {
            logger.w { "searchActivity called while not in success state - cancel ${searchState.value}" }
            return
        }
        _searchState.update {
            SearchState.Loading
        }

        val activities = MutableList(INIT_LOAD_COUNT) {
            activityDataSource.getRandomByParameters(
                types,
                minParticipants,
                maxParticipants,
                minPrice,
                maxPrice,
                minAccessibility,
                maxAccessibility,
            )
        }
        // if no activities found after first load
        if (activities.filterNotNull().isEmpty()) {
            _searchState.update { SearchState.Error }
            return
        }
        //remove duplicates
        for (i in activities.indices) {
            for (j in i + 1 until activities.size) {
                if (activities[i]?.key == activities[j]?.key && activities[i]?.key != null) {
                    logger.w { "Duplicate activity found" }
                    activities[j] = null
                }
            }
        }
        _searchState.update {
            val filteredActivities = activities.filterNotNull()
            if (filteredActivities.size < INIT_LOAD_COUNT * 2.0 / 3) {
                logger.i { "Too much duplicities stopping loadMore" }
                _canLoadMore.update { false }
            }
            if (filteredActivities.isEmpty()) {
                SearchState.Empty
            } else {
                SearchState.Success(
                    filteredActivities, CallParameters(
                        types = types,
                        minParticipants = minParticipants,
                        maxParticipants = maxParticipants,
                        minPrice = minPrice,
                        maxPrice = maxPrice,
                        minAccessibility = minAccessibility,
                        maxAccessibility = maxAccessibility
                    )
                )
            }
        }
    }
}

sealed interface SearchState {
    data object Init : SearchState
    data object Loading : SearchState
    data object Error : SearchState
    data object Empty : SearchState
    open class Success(
        open val activities: List<Activity>, open val calledParameters: CallParameters
    ) : SearchState

    class LoadingMore(
        override val activities: List<Activity>, override val calledParameters: CallParameters
    ) : Success(activities, calledParameters)
}

@Serializable
data class CallParameters(
    val types: List<ActivityType> = emptyList(),
    val minParticipants: Int = 1,
    val maxParticipants: Int = 10,
    val minPrice: Double = 0.0,
    val maxPrice: Double = 1.0,
    val minAccessibility: Double = 0.0,
    val maxAccessibility: Double = 1.0,
) {
    companion object {
        val DEFAULT = CallParameters()
    }
}