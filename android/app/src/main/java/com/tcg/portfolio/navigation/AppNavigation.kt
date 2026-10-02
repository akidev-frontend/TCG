package com.tcg.portfolio.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tcg.portfolio.ui.screens.AddCollectionEntryScreen
import com.tcg.portfolio.ui.screens.CardDetailScreen
import com.tcg.portfolio.ui.screens.CollectionScreen
import com.tcg.portfolio.ui.screens.HomeScreen
import com.tcg.portfolio.ui.screens.ScanScreen
import com.tcg.portfolio.ui.screens.SetDetailScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Scan : Screen("scan")
    object Collection : Screen("collection")
    object SetDetail : Screen("set_detail/{setId}") {
        fun createRoute(setId: Int) = "set_detail/$setId"
    }
    object CardDetail : Screen("card_detail/{cardId}") {
        fun createRoute(cardId: Int) = "card_detail/$cardId"
    }
    object AddCollectionEntry : Screen("add_collection_entry/{cardId}") {
        fun createRoute(cardId: Int) = "add_collection_entry/$cardId"
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Scan.route) {
            ScanScreen(navController = navController)
        }
        composable(Screen.Collection.route) {
            CollectionScreen(navController = navController)
        }
        composable(
            route = Screen.SetDetail.route,
            arguments = listOf(navArgument("setId") { type = NavType.IntType })
        ) { backStackEntry ->
            val setId = backStackEntry.arguments?.getInt("setId") ?: return@composable
            SetDetailScreen(navController = navController, setId = setId)
        }
        composable(
            route = Screen.CardDetail.route,
            arguments = listOf(navArgument("cardId") { type = NavType.IntType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getInt("cardId") ?: return@composable
            CardDetailScreen(navController = navController, cardId = cardId)
        }
        composable(
            route = Screen.AddCollectionEntry.route,
            arguments = listOf(navArgument("cardId") { type = NavType.IntType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getInt("cardId") ?: return@composable
            AddCollectionEntryScreen(navController = navController, cardId = cardId)
        }
    }
}