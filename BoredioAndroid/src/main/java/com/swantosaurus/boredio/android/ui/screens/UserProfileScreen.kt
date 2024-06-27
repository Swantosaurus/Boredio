package com.swantosaurus.boredio.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.swantosaurus.boredio.android.R
import com.swantosaurus.boredio.android.ui.navigation.NavigationDestination
import com.swantosaurus.boredio.android.ui.navigation.navigate
import com.swantosaurus.boredio.android.ui.util.LoadingScreen
import com.swantosaurus.boredio.android.ui.util.relativeToDay
import com.swantosaurus.boredio.screenViewModels.AccountViewModel
import com.swantosaurus.boredio.screenViewModels.CompletedActivities
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt


@Composable
fun UserProfileScreen(
    accountViewModel: AccountViewModel = koinViewModel(),
    navController: NavController
) {
    val completed by accountViewModel.completed.collectAsState()
    val imagesGenerated by accountViewModel.imagesGenerated.collectAsState()
    val totalImageSpace by accountViewModel.totalImageSpace.collectAsState()
    val totalDownloadedActivities by accountViewModel.totalDownloadedActivities.collectAsState()

    LaunchedEffect(Unit) {
        accountViewModel.loadData()
    }
    UserProfileScreenBody(
        completed = completed,
        imagesGenerated = imagesGenerated,
        totalImageSpace = totalImageSpace,
        totalDownloadedActivities = totalDownloadedActivities,
        navigateToStorageManagement = {
            navController.navigate(NavigationDestination.STORAGE)
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreenBody(
    completed: CompletedActivities,
    imagesGenerated: Int,
    totalImageSpace: String,
    totalDownloadedActivities: Int,
    navigateToStorageManagement: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        TopAppBar(title = {
            Text(text = stringResource(id = R.string.userProfile))
        }, scrollBehavior = scrollBehavior)

    }) {
        if (completed.total == -1) {
            LoadingScreen()
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                CompletedActivities(completed)
                Spacer(modifier = Modifier.height(20.dp))
                Storage(
                    totalDownloadedActivities = totalDownloadedActivities,
                    imagesGenerated = imagesGenerated,
                    totalImageSpace = totalImageSpace,
                    navigateToStorageManagement = navigateToStorageManagement
                )
            }
        }
    }
}

@Composable
private fun Storage(
    totalDownloadedActivities: Int,
    imagesGenerated: Int,
    totalImageSpace: String,
    navigateToStorageManagement: () -> Unit
) {
    Column {
        Text(
            modifier = Modifier.align(CenterHorizontally),
            text = stringResource(id = R.string.accountScreenStorageTitle),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(id = R.string.accountScreenStorageLoaclActivities))
                    }
                    append(" ")
                    append(totalDownloadedActivities.toString())
                }
            )
            Column {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.accountScreenStorageImagesDownloaded))
                        }
                        append(" ")
                        append(imagesGenerated.toString())
                    }
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.accountScreenStorageImagesDownloadedMemory))
                        }
                        append(" ")
                        append(totalImageSpace)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = navigateToStorageManagement, modifier = Modifier.align(CenterHorizontally), colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Text(text = stringResource(id = R.string.accountScreenStorageManageButton))
        }
    }
}

@Composable
private fun CompletedActivities(
    completed: CompletedActivities
) {
    Column {
        Text(
            modifier = Modifier.align(CenterHorizontally),
            text = stringResource(id = R.string.accountScreenCompeltedTitle),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stringResource(id = R.string.accountScreenCompeltedToday))
                    }
                    append(" ")
                    append(completed.today.toString())
                }
            )
            Text(text = buildAnnotatedString {
                append(" ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(stringResource(id = R.string.accountScreenCompeltedTotal))
                }
                append(" ")
                append(completed.total.toString())
            })
        }
        Spacer(modifier = Modifier.height(16.dp))
        CompleteGraph(completed = completed)
        Spacer(modifier = Modifier
            .height(16.dp)
            .align(CenterHorizontally))
    }
}

@Composable()
private fun CompleteGraph(completed: CompletedActivities) {
    val ctx = LocalContext.current
    val producer = remember {
        CartesianChartModelProducer.build()
    }
    LaunchedEffect(completed) {
        producer.tryRunTransaction {
            columnSeries {
                series(y = completed.getDays())
            }
        }
    }

    CartesianChartHost(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        chart = rememberCartesianChart(rememberColumnCartesianLayer(),
            startAxis = rememberStartAxis(),
            bottomAxis = rememberBottomAxis(valueFormatter = { value, _, _ ->
                    value.roundToInt().relativeToDay(ctx)
                })),
        modelProducer = producer,
        zoomState = rememberVicoZoomState(zoomEnabled = false)
    )
}

