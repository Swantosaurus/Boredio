package com.swantosaurus.boredio.android.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.swantosaurus.boredio.activity.model.Activity
import com.swantosaurus.boredio.android.R
import com.swantosaurus.boredio.screens.DailyFeedState
import com.swantosaurus.boredio.screens.DailyFeedViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyFeedScreen(
    dailyFeedViewModel: DailyFeedViewModel = koinViewModel()
) {
    val feedState by dailyFeedViewModel.dailyFeedState.collectAsState()
    val rolls by dailyFeedViewModel.rerolls.collectAsState(initial = 3)
    val rerollState by dailyFeedViewModel.dayReloadState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBar(
                scrollBehavior = scrollBehavior,
                rerolls = rolls ?: -2
            )
        }) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                when (feedState) {
                    is DailyFeedState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is DailyFeedState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            TextButton(onClick = { dailyFeedViewModel.retryInit() }) {
                                Text(text = stringResource(id = R.string.reloadButton))
                            }
                        }
                    }

                    is DailyFeedState.Ready -> {
                        DailyFeedBody(
                            (feedState as DailyFeedState.Ready).dailyActivities,
                            dailyFeedViewModel::reroll,
                            dailyFeedViewModel::complete,
                            dailyFeedViewModel::ignore
                        )
                    }
                }
            }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior, rerolls: Int
) {
    TopAppBar(title = {
        Text(text = stringResource(id = R.string.dailyFeedScreenTitle))
    }, scrollBehavior = scrollBehavior, actions = {
        Text(text = stringResource(id = R.string.rerolls) + ": $rerolls ")
    })
}

@Composable
private fun DailyFeedBody(
    activities: List<Activity>,
    reroll: (activity: Activity, onNoRerolls: () -> Unit) -> Unit,
    complete: (activity: Activity, rating: Int?) -> Unit,
    ignore: (activity: Activity) -> Unit
) {
    LazyVerticalGrid(
        contentPadding = PaddingValues(8.dp),
        columns = GridCells.Adaptive(200.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(activities, key = { it.key }) {
            ActivityBox(activity = it, reroll, complete, ignore)
        }
    }
}

data class IgnoreDialogInfo(
    val activity: Activity
)

@Composable
private fun ActivityBox(
    activity: Activity,
    reroll: (activity: Activity, onNoRerolls: () -> Unit) -> Unit,
    complete: (activity: Activity, rating: Int?) -> Unit,
    ignore: (activity: Activity) -> Unit
) {
    var noRollsDialog by remember { mutableStateOf(false) }
    var ignoreDialog by remember { mutableStateOf<IgnoreDialogInfo?>(null) }

    if (noRollsDialog) {
        NoRerollsDialog {
            noRollsDialog = false
        }
    }

    ignoreDialog?.let {
        IgnoreDialog(activity = it.activity, onDismiss = { ignoreDialog = null}) {
            ignore(it.activity)
        }
    }

    Card(modifier = Modifier
        .fillMaxSize()
        .aspectRatio(1f), shape = RoundedCornerShape(10.dp)
    ) {
        Box {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = activity.path,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (activity.completed) Color(75, 181, 67, (255 * 0.6).toInt())
                        else if (activity.ignore) Color(181, 67, 67, (255 * 0.6).toInt())
                        else Color(0, 0, 0, (255 * 0.4).toInt())
                    )
            ) {
                CompositionLocalProvider(value = LocalContentColor provides Color(245, 245, 245)) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.Center),
                        text = activity.activity,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (!activity.completed && !activity.ignore) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        ) {
                            IconButton(onClick = { ignoreDialog = IgnoreDialogInfo(activity) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                            }
                            IconButton(onClick = {
                                reroll(activity) {
                                    Log.d("onNoRerolls", "onNoRerolls")
                                    noRollsDialog = true
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { complete(activity, 0) }) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoRerollsDialog(dismiss: () -> Unit) {
    AlertDialog(onDismissRequest = dismiss, confirmButton = {
        TextButton(onClick = dismiss) {
            Text(text = stringResource(id = R.string.ok))
        }
    }, title = {
        Text(text = stringResource(id = R.string.noRerollsTitle))
    }, text = {
        Text(text = stringResource(id = R.string.noRerollsText))
    })
}

@Composable
fun IgnoreDialog(activity: Activity, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.dismissActivityTitle))
        },
        text = {
            Column {
                Text(text = stringResource(id = R.string.dismissActivityText))
                val str = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                        append(stringResource(id = R.string.activity) + ": ")
                    }
                    append(activity.activity)
                }
                Text(text = str)
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
        confirmButton = {
        TextButton(
            onClick = onConfirm
        ) {
            Text(color = MaterialTheme.colorScheme.error, text = stringResource(id = R.string.dismiss))
        }
    })
}