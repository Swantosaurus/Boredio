package com.swantosaurus.boredio.di

import com.swantosaurus.boredio.SimpleCounterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


actual val platformModule = module {
    viewModel { SimpleCounterViewModel() }
}