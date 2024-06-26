package com.swantosaurus.boredio.android.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swantosaurus.boredio.activity.model.Activity
import com.swantosaurus.boredio.activity.model.ActivityType
import com.swantosaurus.boredio.android.R
import com.swantosaurus.boredio.android.ui.Color.SuccessGreen
import com.swantosaurus.boredio.android.ui.util.LoadingScreen
import com.swantosaurus.boredio.android.ui.util.accessibilityString
import com.swantosaurus.boredio.android.ui.util.priceString
import com.swantosaurus.boredio.screenViewModels.CallParameters
import com.swantosaurus.boredio.screenViewModels.SearchState
import com.swantosaurus.boredio.screenViewModels.SearchViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = koinViewModel()
) {
    val searchState by searchViewModel.searchState.collectAsState()
    val canLoadMore by searchViewModel.canLoadMore.collectAsState()
    val currentParams by searchViewModel.currentParams.collectAsState(CallParameters.DEFAULT)
    val showingBottomSheet by searchViewModel.showingBottomSheet.collectAsState(false)

    SearchScreenContent(
        searchState = searchState,
        currentParams = currentParams,
        canLoadMore = canLoadMore,
        search = searchViewModel::search,
        showingBottomSheet = showingBottomSheet,
        changeParamsCallParameters = searchViewModel::changeParams,
        loadMore = searchViewModel::loadMore,
        reload = searchViewModel::reload,
        save = searchViewModel::saveActivity,
        likeToggle = searchViewModel::likeActivityToggle,
        setBottomSheet = searchViewModel::setBottomSheet
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenContent(
    searchState: SearchState,
    currentParams: CallParameters,
    canLoadMore: Boolean,
    showingBottomSheet: Boolean,
    setBottomSheet: (showing: Boolean?) -> Unit,
    search: () -> Unit,
    changeParamsCallParameters: (CallParameters) -> Unit,
    loadMore: () -> Unit,
    reload: () -> Unit,
    save: (Activity) -> Unit = {},
    likeToggle: (Activity) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val scaffoldState = rememberBottomSheetScaffoldState(rememberStandardBottomSheetState(skipHiddenState = true))

    val mainScope = rememberCoroutineScope()

    LaunchedEffect(showingBottomSheet) {
        if (showingBottomSheet) {
            scaffoldState.bottomSheetState.expand()
        } else {
            scaffoldState.bottomSheetState.partialExpand()
        }
    }

    fun openSearchSheet() {
        setBottomSheet(true)
    }

    fun closeSearchSheet() {
        setBottomSheet(false)
    }

    fun toggleSearchSheet() {
        setBottomSheet(null)
    }

    BottomSheetScaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null, onClick = ::closeSearchSheet),
        topBar = {
            SearchScreenTopBar(scrollBehavior = scrollBehavior, openSearch = ::openSearchSheet)
        },
        scaffoldState = scaffoldState,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                modifier = Modifier.clickable(interactionSource = remember {
                    MutableInteractionSource()
                }, indication = null, onClick = ::toggleSearchSheet),
            )
        },
        sheetContent = {
            SearchScreenFilter(
                currentParams = currentParams,
                changeParamsCallParameters = changeParamsCallParameters,
                search = {
                    search()
                    closeSearchSheet()
                },
            )
        },
    ) { paddingValues ->
        Column(
            Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 8.dp)
        ) {
            when (searchState) {
                is SearchState.Init -> {
                    InitScreen(openSearch = ::openSearchSheet)
                }

                is SearchState.Loading -> {
                    LoadingScreen()
                }

                SearchState.Error -> {
                    ErrorScreen(reload = reload)
                }

                SearchState.Empty -> {
                    EmptyScreen()
                }

                is SearchState.Success -> {
                    SearchScreenBody(
                        searchState = searchState,
                        currentParams = currentParams,
                        canLoadMore = canLoadMore,
                        search = search,
                        changeParamsCallParameters = changeParamsCallParameters,
                        loadMore = loadMore,
                        reload = reload,
                        save = save,
                        likeToggle = likeToggle,
                        bottomSheetHeight = paddingValues.calculateBottomPadding()
                    )
                }
            }
        }
    }
}

@Composable
private fun InitScreen(openSearch: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.searchInitScreenText),
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(8.dp))
        TextButton(onClick = openSearch) {
            Text(text = stringResource(id = R.string.searchInitButton))
        }
    }
}

@Composable
private fun ErrorScreen(reload: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(modifier = Modifier.fillMaxWidth(), text = stringResource(id = R.string.searchErrorScreenText), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.padding(8.dp))
        TextButton(onClick = reload) {
            Text(text = stringResource(id = R.string.searchErrorReloadButton))
        }
    }
}

@Composable
private fun EmptyScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.searchEmptyScreenText))
    }
}

@Composable
private fun SearchScreenBody(
    searchState: SearchState.Success,
    currentParams: CallParameters,
    canLoadMore: Boolean,
    bottomSheetHeight: Dp,
    search: () -> Unit,
    changeParamsCallParameters: (CallParameters) -> Unit,
    loadMore: () -> Unit,
    reload: () -> Unit,
    save: (Activity) -> Unit,
    likeToggle: (Activity) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(searchState.activities, key = { it.key }) {
            ActivityCard(it, save, likeToggle)
        }
        if (canLoadMore) {
            item {
                Box(
                    Modifier
                        .height(64.dp)
                        .fillParentMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    if (searchState is SearchState.LoadingMore) {
                        LoadingScreen()
                    } else {
                        LoadMoreButton(loadMore = loadMore)
                    }
                }
            }
        }
        item {
            Box(Modifier.height(bottomSheetHeight).fillMaxWidth())
        }
    }
}

private const val DELTA = 0.00001

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchScreenFilter(
    currentParams: CallParameters,
    changeParamsCallParameters: (CallParameters) -> Unit,
    search: () -> Unit,
) {
    val (types, minParticipants, maxParticipants, minPrice, maxPrice, minAccessibility, maxAccessibility) = currentParams

    fun resetFilters() {
        changeParamsCallParameters(CallParameters.DEFAULT)
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.searchFilterTypeLabel)
        )
        FlowRow(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ActivityType.entries.forEach {
                TextButton(
                    modifier = Modifier.padding(4.dp),
                    onClick = { changeParamsCallParameters(
                        if(it in types) currentParams.copy(types = (types - it))
                        else currentParams.copy(types = (types + it))
                    ) },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (it in types) SuccessGreen else Color.Unspecified
                    )
                ) {
                    Text(text = it.name.lowercase())
                }
            }
        }
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.searchFilterParticipantsLabel)
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "${currentParams.minParticipants} - ${currentParams.maxParticipants}"
        )
        RangeSlider(
            value = minParticipants.toFloat()..maxParticipants.toFloat(), onValueChange = {
                changeParamsCallParameters(
                    currentParams.copy(
                        minParticipants = it.start.roundToInt(),
                        maxParticipants = it.endInclusive.roundToInt()
                    )
                )
            }, valueRange = 1f..10f, steps = 10
        )

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.searchFilterPriceLabel)
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "${(currentParams.minPrice).priceString()} - ${(currentParams.maxPrice).priceString()}"
        )
        RangeSlider(value = minPrice.toFloat()..maxPrice.toFloat(), onValueChange = {
            changeParamsCallParameters(
                currentParams.copy(
                    minPrice = (it.start.toDouble()).coerceIn(0.0, 1.0),
                    maxPrice = (it.endInclusive.toDouble()).coerceIn(0.0, 1.0)
                )
            )
        }, steps = 9)

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.searchFilterAccessbilityLabel)
        )
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "${(currentParams.minAccessibility).accessibilityString()} - ${(currentParams.maxAccessibility).accessibilityString()}"
        )
        RangeSlider(
            value = minAccessibility.toFloat()..maxAccessibility.toFloat(),
            onValueChange = {
                changeParamsCallParameters(
                    currentParams.copy(
                        minAccessibility = (it.start.toDouble()).coerceIn(0.0, 1.0),
                        maxAccessibility = (it.endInclusive.toDouble()).coerceIn(0.0, 1.0)
                    )
                )
            },
            steps = 9
        )


        Row {
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = ::resetFilters,
                colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(text = stringResource(id = R.string.searchScreenResetFiltersButton))
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = search,
                colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(text = stringResource(id = R.string.searchScreenFilterButton))
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ActivityCard(
    activity: Activity, save: (Activity) -> Unit, likeToggle: (Activity) -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                text = activity.activity,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium
            )
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Column(Modifier.padding(horizontal = 16.dp)) {
                BoxWithConstraints {
                    val width = maxWidth
                    Row {
                        Column(modifier = Modifier.width(width * 0.5f)) {
                            Text(stringResource(id = R.string.searchCardTypeLabel))
                            Text(stringResource(id = R.string.searchCardParticipants))
                            Text(stringResource(id = R.string.searchCardPrice))
                            Text(stringResource(id = R.string.searchCardAccessibility))
                        }
                        Column {
                            Text(activity.type.name.mapIndexed { index, c -> if (index == 0) c.uppercase() else c.lowercase() }
                                .joinToString(separator = ""))
                            Text(activity.participants.toString())
                            Text(activity.price.toString())
                            Text(activity.accessibility.accessibilityString())
                        }
                    }
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Row(Modifier.padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { save(activity) }, enabled = !activity.isStored) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_file_download_24),
                        contentDescription = null
                    )
                }
                IconButton(onClick = { likeToggle(activity) }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (activity.favorite) Color.Yellow else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadMoreButton(loadMore: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TextButton(onClick = loadMore) {
            Text(text = stringResource(id = R.string.searchLoadMoreButton))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenTopBar(scrollBehavior: TopAppBarScrollBehavior, openSearch: () -> Unit) {
    TopAppBar(title = {
        Text(text = stringResource(id = R.string.searchScreenTitle))
    }, actions = {
        IconButton(onClick = {
            openSearch()
        }) {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        }
    }, scrollBehavior = scrollBehavior
    )
}