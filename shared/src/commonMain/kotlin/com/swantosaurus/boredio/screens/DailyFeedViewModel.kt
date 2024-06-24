package com.swantosaurus.boredio.screens

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import co.touchlab.kermit.Logger
import com.swantosaurus.boredio.ViewModel
import com.swantosaurus.boredio.activity.dataSource.ActivityDataSource
import com.swantosaurus.boredio.activity.model.Activity
import com.swantosaurus.boredio.util.toDateTime
import com.swantosaurus.boredio.util.toMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val REROLLS_KEY_LAST_UPDATE = "rerolls_last_update"
private const val REROLLS_KEY = "reroll_count"

private const val DAILY_REROLL_CNT = 2

/** IOS supports only MainScope this is single thread scope that just plans tasks in queue for single executor */
/** https://github.com/Kotlin/kotlinx.coroutines/issues/462 */
@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val backgroundSingleThreadScope = CoroutineScope(newSingleThreadContext("DailyFeedViewModelBackgroundScope"))


class DailyFeedViewModel(
    private val activityDataSource: ActivityDataSource,
    private val preferences: DataStore<Preferences>,
) : ViewModel() {
    private val logger = Logger.withTag("DailyFeedViewModel")

    /** DAILY FEED */
    private val _dailyFeedState = MutableStateFlow<DailyFeedState>(DailyFeedState.Loading)
    val dailyFeedState = _dailyFeedState.asStateFlow()

    /** REROLS */
    val rerolls = preferences.data.map { it[rerollsKey] }
    private val rerollsKey = intPreferencesKey(REROLLS_KEY)
    private val rerollsLastUpdateKey = longPreferencesKey(REROLLS_KEY_LAST_UPDATE)

    /** ON DAY CHANGE */
    private val _dayResetState = MutableStateFlow<DayResetState>(DayResetState.Dismissed)
    val dayReloadState = _dayResetState.asStateFlow()

    private lateinit var lastUpdate: LocalDateTime

    init {
        initialize()
    }

    fun reroll(activity: Activity, onNoRerolls: () -> Unit) = reroll(activity.key, onNoRerolls)

    fun reroll(key: String, onNoRerolls: () -> Unit) {
        backgroundSingleThreadScope.launch {
            rerollInternal(key, onNoRerolls = onNoRerolls)
        }
    }

    fun retryInit() = initialize()

    fun complete(activity: Activity, rating: Int?) = complete(activity.key, rating)

    fun complete(key: String, rating: Int?) {
        if (!checkIsUpToDate()) {
            //accent completion even on day change
            backgroundSingleThreadScope.launch {
                activityDataSource.storeActivity(
                    activityDataSource.getActivityByKey(key)!!
                        .copy(completed = true, userRating = rating)
                )
            }
            _dayResetState.update {
                // tells user that even tho day changed before completing
                // the activity was still counted as completed
                DayResetState.ActiveStillCompleted
            }
            return
        }

        backgroundSingleThreadScope.launch {
            _dailyFeedState.update { currentState ->
                if (currentState !is DailyFeedState.Ready) {
                    return@update DailyFeedState.Error
                }
                DailyFeedState.Ready(currentState.dailyActivities.map {
                    if (it.key != key) return@map it

                    val completeDate =
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

                    activityDataSource.storeActivity(
                        it.copy(
                            completed = true, completeDate = completeDate, userRating = rating
                        )
                    )
                })
            }
        }
    }

    fun ignore(activity: Activity) = ignore(activity.key)

    fun ignore(key: String){
        checkIsUpToDate()
        backgroundSingleThreadScope.launch {
            _dailyFeedState.update { currentState ->
                if (currentState !is DailyFeedState.Ready) {
                    return@update DailyFeedState.Error
                }

                DailyFeedState.Ready(currentState.dailyActivities.map {
                    if (it.key != key) return@map it

                    activityDataSource.storeActivity(
                        it.copy(
                            ignore = true
                        )
                    )
                })
            }
        }
    }

    private suspend fun rerollInternal(key: String, onNoRerolls: () -> Unit) {
        if (!checkIsUpToDate()) {
            // on next day reroll cnt is reset automatically with whole new feed
            return
        }


        preferences.edit { prefs ->
            val rerollsFrom = prefs[rerollsKey]!!
            logger.d { "rerolls from $rerollsFrom" }
            if(rerollsFrom <= 0) {
                onNoRerolls()
                return@edit
            }
            _dailyFeedState.update { currentFeed ->
                if (currentFeed !is DailyFeedState.Ready) {
                    return@update DailyFeedState.Error
                }

                val newActivity =
                    activityDataSource.getNewRandom { activityWithImage ->
                        updateOnImage(activityWithImage)
                    } ?: return@update DailyFeedState.Error

                DailyFeedState.Ready(currentFeed.dailyActivities.map { activity ->
                    if (activity.key == key) {
                        prefs[rerollsKey] = rerollsFrom - 1
                        activityDataSource.storeActivity(activity.copy(isDailyFeed = false))
                        newActivity
                    } else {
                        activity
                    }
                })
            }
        }
    }

    /**
     * resets daily feed on midnight and updates rerolls
     *
     * @returns false if new day starts and resets daily feed
     */
    private fun checkIsUpToDate(): Boolean {
        if (lastUpdate.date != Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).date
        ) {
            initialize()
            return false
        } else {
            return true
        }
    }

    private fun initialize() {
        _dailyFeedState.update {
            DailyFeedState.Loading
        }
        logger.i { "daily feed (loads or reloads) for screen" }
        lastUpdate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        backgroundSingleThreadScope.launch {
            _dailyFeedState.update {
                val feed = activityDataSource.getDailyFeed { activityWithImage ->
                    updateOnImage(activityWithImage)
                }
                if (feed.isNullOrEmpty()) {
                    DailyFeedState.Error
                } else {
                    DailyFeedState.Ready(feed)
                }
            }
        }

        backgroundSingleThreadScope.launch {
            preferences.data.firstOrNull()?.let {
                val rerolls = it[rerollsKey]
                val rrLastUpdate = (it[rerollsLastUpdateKey] ?: 0L).toDateTime()

                logger.d { "preferences typesafe keys rerolls:$rerolls, $rrLastUpdate " }

                if (rerolls == null || rrLastUpdate.date != lastUpdate.date) {
                    logger.i { "daily reroll resets" }
                    preferences.edit { update ->
                        update[rerollsKey] = DAILY_REROLL_CNT
                        update[rerollsLastUpdateKey] = lastUpdate.toMillis()
                    }
                }
            }
        }
    }

    /** updates activity with image after async image loading*/
    private fun updateOnImage(activityWithImage: Activity) {
        if(_dailyFeedState.value !is DailyFeedState.Ready) {
            return
        }
        _dailyFeedState.update { currentState ->
            if (currentState !is DailyFeedState.Ready) {
                return@update DailyFeedState.Error
            }
            DailyFeedState.Ready(currentState.dailyActivities.map {
                if (it.key == activityWithImage.key) {
                    activityWithImage
                } else {
                    it
                }
            })
        }
    }
}

sealed interface DailyFeedState {
    data object Loading : DailyFeedState
    data object Error : DailyFeedState
    data class Ready(
        val dailyActivities: List<Activity>
    ) : DailyFeedState
}

sealed interface DayResetState {
    data object Dismissed : DayResetState
    data object ActiveStillCompleted : DayResetState
}
