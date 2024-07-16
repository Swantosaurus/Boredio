package com.swantosaurus.boredio.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.swantosaurus.boredio.activity.model.Activity
import com.swantosaurus.boredio.android.R
import com.swantosaurus.boredio.android.ui.util.LoadingScreen
import com.swantosaurus.boredio.android.ui.util.accessibilityString
import com.swantosaurus.boredio.android.ui.util.priceString
import com.swantosaurus.boredio.screenViewModels.StorageActivities
import com.swantosaurus.boredio.screenViewModels.StorageFilter
import com.swantosaurus.boredio.screenViewModels.StorageViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    storageViewModel: StorageViewModel = koinViewModel(),
    navController: NavController
) {
    val selectedFilter by storageViewModel.selectedFilter.collectAsState()
    val activities by storageViewModel.activities.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.storageScreenTitle)) }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                }
            })
        },
        bottomBar = {
            TabBar(selectedFilter = selectedFilter, selectFilter = storageViewModel::selectFilter)
        }
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)){
            if(activities is StorageActivities.Loading) {
                LoadingScreen()
            }
            (activities as? StorageActivities.Success)?.let { rdy ->
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = rdy.activities,
                        key = { it.key },
                    ) {
                        ActivityCard(
                            activity = it,
                            delete = storageViewModel::delete,
                            favoriteToggle = storageViewModel::favoriteToggle,
                            ignoreToggle = storageViewModel::ignoreToggle,
                            deleteImage = storageViewModel::deleteImage
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityCard(
    activity: Activity,
    delete: (Activity) -> Unit,
    favoriteToggle: (Activity) -> Unit,
    ignoreToggle: (Activity) -> Unit,
    deleteImage: (Activity) -> Unit
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
                            Text(activity.price.priceString())
                            Text(activity.accessibility.accessibilityString())
                        }
                    }
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                IconButton(onClick = { favoriteToggle(activity) }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (activity.favorite) Color.Yellow else Color.Gray
                    )
                }
                IconButton(onClick = { ignoreToggle(activity) }) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (activity.ignore) Color.Red else Color.Gray
                    )
                }
                IconButton(onClick = { deleteImage(activity) },
                    enabled = !(activity.path == null || activity.isDailyFeed),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Red)
                ) {
                    Icon(
                        painterResource(id = R.drawable.baseline_image_not_supported_24),
                        contentDescription = null,
                    )
                }
                IconButton(
                    onClick = { delete(activity) }, enabled = !activity.isDailyFeed
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                    )
                }

            }
        }
    }
}

@Composable
private fun TabBar(
    selectedFilter: StorageFilter,
    selectFilter: (StorageFilter) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Filled.List, contentDescription = null) },
            label = { Text(stringResource(id = R.string.storageScreenFilterAll)) },
            selected = selectedFilter == StorageFilter.All,
            onClick = { selectFilter(StorageFilter.All) }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Filled.Star, contentDescription = null) },
            label = { Text(stringResource(id = R.string.storageScreenFilterFavorite)) },
            selected = selectedFilter == StorageFilter.Favorite,
            onClick = { selectFilter(StorageFilter.Favorite) }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Outlined.DateRange, contentDescription = null) },
            label = { Text(stringResource(id = R.string.storageScreenFilterImage)) },
            selected = selectedFilter == StorageFilter.WithImage,
            onClick = { selectFilter(StorageFilter.WithImage) }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Filled.Warning, contentDescription = null) },
            label = { Text(stringResource(id = R.string.storageScreenFilterIgnored)) },
            selected = selectedFilter == StorageFilter.Ignored,
            onClick = { selectFilter(StorageFilter.Ignored) }
        )
    }
}