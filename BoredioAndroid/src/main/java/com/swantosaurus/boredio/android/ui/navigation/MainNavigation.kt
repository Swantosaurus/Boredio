package com.swantosaurus.boredio.android.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swantosaurus.boredio.android.R
import com.swantosaurus.boredio.android.ui.screens.AboutScreen
import com.swantosaurus.boredio.android.ui.screens.DailyFeedScreen
import com.swantosaurus.boredio.android.ui.screens.SearchScreen
import com.swantosaurus.boredio.android.ui.screens.StorageScreen
import com.swantosaurus.boredio.android.ui.screens.UserProfileScreen
import kotlinx.coroutines.flow.map


sealed interface NavigationDestination {
    val route: String

    data object DAILY_FEED : NavigationDestination {
        override val route: String
            get() = "day_feed"
    }

    data object SEARCH : NavigationDestination {
        override val route: String
            get() = "search"
    }

    data object ACCOUNT : NavigationDestination {
        override val route: String
            get() = "account"
    }

    data object ABOUT : NavigationDestination {
        override val route: String
            get() = "about"
    }

    data object STORAGE : NavigationDestination {
        override val route: String
            get() = "storage"
    }
}

enum class TabBarDestinations(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val bottomBarIconResSelected: Int,
    @DrawableRes val bottomBarIconResUnselected: Int
) {
    DAY_FEED(
        NavigationDestination.DAILY_FEED.route,
        R.string.dayFeedTabBarTitle,
        R.drawable.baseline_event_note_24,
        R.drawable.outline_event_note_24
    ),
    ACCOUNT(
        NavigationDestination.ACCOUNT.route,
        R.string.accountTabBarTitle,
        R.drawable.baseline_account_circle_24,
        R.drawable.outline_account_circle_24
    ),
    SEARCH(
        NavigationDestination.SEARCH.route,
        R.string.searchTabBarTitle,
        R.drawable.twotone_image_search_24,
        R.drawable.outline_image_search_24
    ),
    ABOUT(
        NavigationDestination.ABOUT.route,
        R.string.aboutTabBarTitle,
        R.drawable.baseline_info_24,
        R.drawable.baseline_info_outline_24
    )
}


fun NavController.navigate(to: NavigationDestination) {
    navigate(to.route)
}

@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "tab_bar") {

//        composable(NavigationDestination.DAILY_FEED) {
//            DailyFeedScreen()
//        }
//        composable(NavigationDestination.ACCOUNT) {
//            UserProfileScreen(navController = navController)
//        }
//
//        composable(NavigationDestination.SEARCH) {
//            SearchScreen()
//        }
//
//        composable(NavigationDestination.ABOUT) {
//            AboutScreen()
//        }

        composable(route = "tab_bar") {
           TabBar(navController)
        }
        composable(NavigationDestination.STORAGE) {
            StorageScreen(navController = navController)
        }
    }
}

@Composable
private fun TabBar(
    mainNavController: NavController
) {
    val tabBarNavController = rememberNavController()

    val currentTabBarRoute by tabBarNavController.currentBackStackEntryFlow.map { it.destination.route }
        .collectAsState(
            initial = TabBarDestinations.DAY_FEED.route
        )
    Scaffold(bottomBar = {
        NavBar(navController = tabBarNavController, currentRoute = currentTabBarRoute?: "")
    }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = padding.calculateBottomPadding(),
                    start = padding.calculateLeftPadding(LocalLayoutDirection.current),
                    end = padding.calculateRightPadding(LocalLayoutDirection.current),
                )
        ) {
            TabBarNavigation(mainNavController = mainNavController, tabBarNavHostController = tabBarNavController)
        }
    }
}

@Composable
private fun TabBarNavigation(mainNavController: NavController, tabBarNavHostController: NavHostController) {
    NavHost(tabBarNavHostController, startDestination = NavigationDestination.DAILY_FEED.route){
        composable(NavigationDestination.DAILY_FEED) {
            DailyFeedScreen()
        }
        composable(NavigationDestination.ACCOUNT) {
            UserProfileScreen(navController = mainNavController)
        }

        composable(NavigationDestination.SEARCH) {
            SearchScreen()
        }

        composable(NavigationDestination.ABOUT) {
            AboutScreen()
        }
    }

}

@Composable
private fun NavBar(navController: NavController, currentRoute:String) {
    NavigationBar {
        TabBarDestinations.entries.forEach { destination ->
            val isSelected = currentRoute == destination.route
            NavigationBarItem(icon = {
                if (isSelected) {
                    Icon(
                        painter = painterResource(id = destination.bottomBarIconResSelected),
                        contentDescription = null
                    )
                } else {
                    Icon(
                        painter = painterResource(id = destination.bottomBarIconResUnselected),
                        contentDescription = null
                    )
                }
            },
                label = {
                    Text(text = stringResource(id = destination.title))
                },
                selected = navController.currentDestination?.route == destination.route,
                onClick = {
                    navController.saveState()
                    navController.navigate(destination.route){
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        this.restoreState = true
                        this.launchSingleTop = true
                    }
                })

        }
    }
}

private fun NavGraphBuilder.composable(
    route: NavigationDestination,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = enterTransition,
    popExitTransition: (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route.route,
        arguments,
        deepLinks,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition,
        content
    )
}