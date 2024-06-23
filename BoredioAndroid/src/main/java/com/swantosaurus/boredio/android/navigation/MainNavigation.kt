package com.swantosaurus.boredio.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swantosaurus.boredio.android.ui.DailyFeedScreen


@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "day_feed"){
        composable("day_feed") {
            DailyFeedScreen()
        }
    }
}