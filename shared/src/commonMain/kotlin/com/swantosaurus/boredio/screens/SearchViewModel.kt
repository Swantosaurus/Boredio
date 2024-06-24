package com.swantosaurus.boredio.screens

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val backgroundSingleThreadScope =
    CoroutineScope(newSingleThreadContext("SearchViewModelBackgroundScope"))

private val logger = Logger.withTag("SearchViewModel")

private const val INIT_LOAD_COUNT = 20
private const val NEXT_PAGE_LOAD = 10

// @Synchronized wont work maybe this will
// but who knows cuz it uses again some coroutines stuff that might not work on native
@OptIn(InternalCoroutinesApi::class)
private object Lock : SynchronizedObject()


class SearchViewModel(
    private val activityDataSource: ActivityDataSource
) : ViewModel() {


    private val _searchState = MutableStateFlow<SearchState>(SearchState.Init)
    val searchState = _searchState.asStateFlow()

    private var _canLoadMore = MutableStateFlow<Boolean>(true)
    private var canLoadMore = _canLoadMore.asStateFlow()

    @OptIn(InternalCoroutinesApi::class)
    fun searchActivity(
        types: List<ActivityType> = emptyList(),
        minParticipants: Int? = null,
        maxParticipants: Int? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        minAccessibility: Double? = null,
        maxAccessibility: Double? = null,
    ) {
        if (searchState.value !is SearchState.Success) {
            logger.w { "searchActivity called while not in success state - cancel" }
            return
        }
        backgroundSingleThreadScope.launch {
            synchronized(Lock) {
                if (searchState.value !is SearchState.Success) {
                    logger.w { "searchActivity called while not in success state - cancel" }
                    return@launch
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
                    return@launch
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
                            filteredActivities, CalledParameters(
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
    }

    fun saveActivity(activity: Activity) {
        backgroundSingleThreadScope.launch {
            activityDataSource.storeActivity(activity)
        }
    }

    fun likeActivity(activity: Activity) {
        backgroundSingleThreadScope.launch {
            activityDataSource.storeActivity(activity.copy(favorite = true))
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
            synchronized(Lock) {
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
                        (searchState.value as SearchState.Success).calledParameters
                    )
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
                    if (filteredActivities.size < NEXT_PAGE_LOAD * 2.0 / 3) {
                        logger.i { "Too much duplicities stopping loadMore" }
                        _canLoadMore.update { false }
                    }
                    SearchState.Success(
                        (searchState.value as SearchState.LoadingMore).activities + filteredActivities,
                        (searchState.value as SearchState.LoadingMore).calledParameters
                    )
                }
            }
        }
    }

    private suspend fun ActivityDataSource.getRandomByParameters(calledParameters: CalledParameters) =
        getRandomByParameters(
            calledParameters.types,
            calledParameters.minParticipants,
            calledParameters.maxParticipants,
            calledParameters.minPrice,
            calledParameters.maxPrice,
            calledParameters.minAccessibility,
            calledParameters.maxAccessibility,
        )
}

sealed interface SearchState {
    object Init : SearchState
    object Loading : SearchState
    object Error : SearchState
    object Empty : SearchState
    open class Success(
        open val activities: List<Activity>, open val calledParameters: CalledParameters
    ) : SearchState

    class LoadingMore(
        override val activities: List<Activity>, override val calledParameters: CalledParameters
    ) : Success(activities, calledParameters)
}

data class CalledParameters(
    val types: List<ActivityType> = emptyList(),
    val minParticipants: Int? = null,
    val maxParticipants: Int? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minAccessibility: Double? = null,
    val maxAccessibility: Double? = null,
)