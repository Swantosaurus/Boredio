package com.swantosaurus.boredio

import co.touchlab.kermit.Logger
import androidx.lifecycle.ViewModel as AndroidXViewModel

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual abstract class ViewModel actual constructor() : AndroidXViewModel() {
    init {
        Logger.withTag("VM").i { "ViewModel created $this" }
    }
    actual override fun onCleared() {
        Logger.withTag("VM").i { "ViewModel cleared $this" }
    }
}