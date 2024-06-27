package com.swantosaurus.boredio.screenViewModels

import co.touchlab.kermit.Logger
import com.swantosaurus.boredio.ViewModel
import com.swantosaurus.boredio.activity.dataSource.ActivityDataSource
import com.swantosaurus.boredio.activity.model.Activity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

private val logger = Logger.withTag("StorageViewModel")

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val backgroundSingleThreadScope =
    CoroutineScope(newSingleThreadContext("StorageViewModelBackgroundScope"))


class StorageViewModel(
    private val activityDataSource: ActivityDataSource
) : ViewModel() {
    private val _selectedFilter = MutableStateFlow<StorageFilter>(StorageFilter.All)
    val selectedFilter: StateFlow<StorageFilter> = _selectedFilter.asStateFlow()

    private val allActivities = MutableStateFlow<List<Activity>>(emptyList())
    val activities: StateFlow<StorageActivities> = allActivities.map {
        if(it.isEmpty()) StorageActivities.Loading
        else StorageActivities.Success(it)
    }.combine(_selectedFilter) { state, filter ->
        if(state is StorageActivities.Success) {
            when(filter) {
                StorageFilter.All -> state
                StorageFilter.Favorite -> StorageActivities.Success(state.activities.filter { it.favorite })
                StorageFilter.Ignored -> StorageActivities.Success(state.activities.filter { it.ignore })
                StorageFilter.WithImage -> StorageActivities.Success(state.activities.filter { it.path != null })
            }
        } else state
    }.stateIn(CoroutineScope(Dispatchers.Main), started = SharingStarted.Eagerly, StorageActivities.Loading)

    init {
        backgroundSingleThreadScope.launch {
            updateActivities()
        }
    }

    fun selectFilter(filter: StorageFilter) {
        _selectedFilter.value = filter
    }


    fun delete(activity: Activity) {
        backgroundSingleThreadScope.launch {
            activityDataSource.deleteActivity(activity)

            updateActivities()
        }
    }

    fun favoriteToggle(activity: Activity) {
        backgroundSingleThreadScope.launch {
            activityDataSource.storeActivity(activity.copy(favorite = !activity.favorite))

            updateActivities()
        }
    }

    fun ignoreToggle(activity: Activity) {
        backgroundSingleThreadScope.launch {
            activityDataSource.storeActivity(activity.copy(ignore = !activity.ignore))

            updateActivities()
        }
    }

    fun deleteImage(activity: Activity) {
        backgroundSingleThreadScope.launch {
            activityDataSource.deleteImage(activity)

            updateActivities()
        }
    }

    private suspend fun updateActivities() {
        allActivities.update {
            activityDataSource.getAllStoredActivities()
        }
    }
}

sealed interface StorageFilter {
    object All : StorageFilter
    object Favorite : StorageFilter
    object Ignored : StorageFilter
    object WithImage : StorageFilter
}

sealed interface StorageActivities {
    data object Loading : StorageActivities
    data class Success(val activities: List<Activity>) : StorageActivities
}