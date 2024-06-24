package com.swantosaurus.boredio.android.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.swantosaurus.boredio.android.R
import com.swantosaurus.boredio.android.ui.ActivityFinderScreen
import com.swantosaurus.boredio.android.ui.DailyFeedScreen

enum class NavigationDestinations(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val bottomBarIconResSelected: Int,
    @DrawableRes val bottomBarIconResUnselected: Int
) {
    DAY_FEED("day_feed", R.string.dayFeedTabBarTitle, R.drawable.baseline_event_note_24, R.drawable.outline_event_note_24),
    ACCOUNT("account", R.string.accountTabBarTitle, R.drawable.baseline_account_circle_24, R.drawable.outline_account_circle_24),
    SEARCH("search", R.string.searchTabBarTitle, R.drawable.twotone_image_search_24, R.drawable.outline_image_search_24)
}


@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavigationDestinations.DAY_FEED.route){
        composable(NavigationDestinations.DAY_FEED.route) {
            DailyFeedScreen()
        }

        composable(NavigationDestinations.ACCOUNT.route) {
            ActivityFinderScreen()
        }

        composable(NavigationDestinations.SEARCH.route) {
            Text("Search")
        }
    }
}