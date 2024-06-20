package com.swantosaurus.boredio

actual abstract class ViewModel actual constructor() {

    /**
     * Override this to do any cleanup immediately before the internal [CoroutineScope][kotlinx.coroutines.CoroutineScope]
     * is cancelled.
     */
    protected actual open fun onCleared() {
    }

    fun clear() {
        onCleared()
    }
}
