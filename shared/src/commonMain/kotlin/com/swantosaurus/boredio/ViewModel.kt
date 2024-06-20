package com.swantosaurus.boredio

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect abstract class ViewModel() {
    protected open fun onCleared()
}

