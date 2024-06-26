package com.swantosaurus.boredio.android.ui.util

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableSection(
    isExpanded: MutableTransitionState<Boolean>,
    toggleExpanded: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val icon = if (isExpanded.currentState) {
        Icons.Outlined.KeyboardArrowUp
    } else {
        Icons.Outlined.KeyboardArrowDown
    }
    Column(
        modifier
            .clickable { toggleExpanded() }
            .background(MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            var size by remember { mutableStateOf(IntSize.Zero) }
            Row(
                modifier = Modifier.onSizeChanged {
                        Log.d("onSizeChanged", "size: ${it.height}x ${it.width}")
                        size = it
                    }, verticalAlignment = Alignment.CenterVertically,
            ) {
                title()
                Spacer(Modifier.weight(1f))
                Icon(imageVector = icon,
                    contentDescription = null,
                    Modifier.height(with(LocalDensity.current) {
                        (size.height - 20).toDp()
                    }))
                Spacer(modifier = Modifier.width(16.dp))
            }
        }

        AnimatedVisibility(visibleState = isExpanded) {
            Column {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                content()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}