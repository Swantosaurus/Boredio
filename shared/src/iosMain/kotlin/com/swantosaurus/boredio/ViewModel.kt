package com.swantosaurus.boredio

import co.touchlab.kermit.Logger

actual abstract class ViewModel actual constructor() {

    init {
        Logger.withTag("VM").i { "ViewModel created $this" }
    }
    /**
     * Override this to do any cleanup immediately before the internal [CoroutineScope][kotlinx.coroutines.CoroutineScope]
     * is cancelled.
     */
    protected actual open fun onCleared() {
        Logger.withTag("VM").i { "ViewModel cleared $this"}
    }

    fun clear() {
        onCleared()
    }
}
