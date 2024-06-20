package com.swantosaurus.boredio

import com.swantosaurus.boredio.activity.dataSource.ActivityDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

class SimpleCounterViewModel(private val activityDataSource: ActivityDataSource): ViewModel() {
    private val _count = MutableStateFlow(0)

    val count : StateFlow<Int> = _count.asStateFlow()

    fun increment() {
        //CoroutineScope(Dispatchers.IO).launch {
        println("increment")
        runBlocking {
            val activity = activityDataSource.getNewRandom()
            println(activity)
        }
        _count.update {
            it.inc()
        }
    }

    fun decrement() {
        _count.update {
            it.dec()
        }
    }
}
