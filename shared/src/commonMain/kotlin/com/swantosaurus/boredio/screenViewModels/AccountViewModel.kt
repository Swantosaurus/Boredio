package com.swantosaurus.boredio.screenViewModels

import co.touchlab.kermit.Logger
import com.swantosaurus.boredio.ViewModel
import com.swantosaurus.boredio.activity.dataSource.ActivityDataSource
import com.swantosaurus.boredio.activity.dataSource.imageGenerating.local.GeneratedImageFileSystem
import com.swantosaurus.boredio.util.format
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private val backgroundSingleThreadScope =
    CoroutineScope(newSingleThreadContext("AccountViewModelBackgroundScope"))

private val logger = Logger.withTag("AccountViewModel")

class AccountViewModel(
    private val dataSource: ActivityDataSource,
    private val generatedImageFileSystem: GeneratedImageFileSystem
) : ViewModel() {

    private val _completed : MutableStateFlow<CompletedActivities> =
        MutableStateFlow(CompletedActivities.Empty)
    val completed = _completed.asStateFlow()

    private val _imagesGenerated = MutableStateFlow<Int>(0)
    val imagesGenerated = _imagesGenerated.asStateFlow()

    private val _totalImageSpace = MutableStateFlow<String>("")
    val totalImageSpace = _totalImageSpace.asStateFlow()

    private val _totalDownloadedActivities = MutableStateFlow<Int>(0)
    val totalDownloadedActivities = _totalDownloadedActivities.asStateFlow()

    fun loadData() {
        loadCompleted()
        loadImagesGenerated()
        loadTotalImageSpace()
        loadTotalDownloadedActivities()

    }

    private fun loadCompleted() {
        backgroundSingleThreadScope.launch {
            val startOfTomorrow = Clock.System.now().plus(1.days)
                .toLocalDateTime(TimeZone.currentSystemDefault()).date.atStartOfDayIn(TimeZone.currentSystemDefault())


            val dates = List(9) { i ->
                with(TimeZone.currentSystemDefault()) {
                    val dayStart =
                        startOfTomorrow.minus(i.days).toLocalDateTime().date.atStartOfDayIn(this)
                            .toEpochMilliseconds()
                    dayStart
                }
            }

            val completed = List(8) { i ->
                dataSource.getCompletedBetweenDates(dates[i + 1], dates[i]).size
            }

            val allCompleted = dataSource.getAllCompletedActivities()


            _completed.update {
                CompletedActivities(
                    total = allCompleted.size,
                    today = completed[0],
                    todayM1 = completed[1],
                    todayM2 = completed[2],
                    todayM3 = completed[3],
                    todayM4 = completed[4],
                    todayM5 = completed[5],
                    todayM6 = completed[6],
                    todayM7 = completed[7],
                )
            }
        }
    }

    private fun loadImagesGenerated() {
        backgroundSingleThreadScope.launch {
            val allActivities = dataSource.getAllWithGeneratedImage()
            _imagesGenerated.update { allActivities.size }
        }
    }

    private fun loadTotalImageSpace() {
        backgroundSingleThreadScope.launch {
            _totalImageSpace.update {
                generatedImageFileSystem.getSizeOf(null).let {
                    if (it == -1L) {
                        ""
                    } else {
                        bytesToHumanReadableSize(it.toDouble())
                    }
                }
            }
        }
    }


    fun bytesToHumanReadableSize(bytes: Double) = when {
        bytes >= 1 shl 30 -> (bytes / (1 shl 30)).format(2) + " GB"
        bytes >= 1 shl 20 -> (bytes / (1 shl 20)).format(2) + " MB"
        bytes >= 1 shl 10 -> (bytes / (1 shl 10)).format(2) + " kB"
        else -> "$bytes bytes"
    }



    private fun loadTotalDownloadedActivities() {
        backgroundSingleThreadScope.launch {
            val allActivities = dataSource.getAllStoredActivities()
            _totalDownloadedActivities.update { allActivities.size }
        }
    }


}

data class CompletedActivities(
    val total: Int,
    val today: Int,
    val todayM1: Int,
    val todayM2: Int,
    val todayM3: Int,
    val todayM4: Int,
    val todayM5: Int,
    val todayM6: Int,
    val todayM7: Int,
) {
    fun maxCompletedInPastDays() {
        maxOf(today, todayM1, todayM2, todayM3, todayM4, todayM5, todayM6, todayM7)
    }

    fun getDays(): List<Int> =
        listOf(today, todayM1, todayM2, todayM3, todayM4, todayM5, todayM6, todayM7)

    companion object {
        val Empty = CompletedActivities(-1, 0, 0, 0, 0, 0, 0, 0, 0)
    }

}