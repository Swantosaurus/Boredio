package com.swantosaurus.boredio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SimpleCounterViewModel: ViewModel() {
    private val _count = MutableStateFlow(0)

    val count : StateFlow<Int> = _count.asStateFlow()


    fun increment() {
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
