package com.swantosaurus.boredio.android.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.swantosaurus.boredio.android.R

@Composable
fun Double.accessibilityString(): String {
    return when (this) {
        in -0.5..0.2 -> stringResource(id = R.string.mesureVeryHigh)
        in 0.2..0.4 -> stringResource(id = R.string.mesureHigh)
        in 0.4..0.6 -> stringResource(id = R.string.mesureMedium)
        in 0.6..0.8 -> stringResource(id = R.string.mesureLow)
        else -> stringResource(id = R.string.mesureVeryLow)
    }
}