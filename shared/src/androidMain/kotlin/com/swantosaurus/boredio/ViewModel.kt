package com.swantosaurus.boredio

import androidx.lifecycle.ViewModel as AndroidXViewModel

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual abstract class ViewModel actual constructor() : AndroidXViewModel() {
    actual override fun onCleared() {}
}