@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
package com.github.pokatomnik.davno.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.github.pokatomnik.davno.services.serializer.Serializer

class Navigation(
    val navController: NavHostController,
    private val serializer: Serializer
) {
    private fun NavHostController.navigateDistinct(route: String) {
        navigate(route) { launchSingleTop = true; }
    }

    private fun NavHostController.navigateAllowSame(route: String) {
        navigate(route)
    }

    private fun NavDestination?.on(route: String): Boolean {
        return this?.hierarchy?.any { it.route == route } == true
    }

    fun navigateBack(): Boolean {
        return navController.popBackStack()
    }

    @Composable
    private fun rememberCurrentDestination(): NavDestination? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination
    }

    val defaultRoute: Route
        get() = vaultSelectorRoute

    val vaultSelectorRoute = object : RouteNoParameters {
        private val routePath = "/"

        @Composable
        override fun on(): Boolean {
            val currentDestination = rememberCurrentDestination()
            return currentDestination.on(routePath)
        }

        override fun navigate() {
            navController.navigateDistinct(routePath)
        }

        @Composable
        override fun Params(content: @Composable () -> Unit) {
            content()
        }

        override val route: String
            get() = routePath
    }

    val vaultAddRoute = object : RouteNoParameters {
        private val routePath = "/vault/add"

        @Composable
        override fun on(): Boolean {
            val currentDestination = rememberCurrentDestination()
            return currentDestination.on(routePath)
        }

        override fun navigate() {
            navController.navigateDistinct(routePath)
        }

        @Composable
        override fun Params(content: @Composable () -> Unit) {
            content()
        }

        override val route: String
            get() = routePath
    }

    val vaultEditRoute = object : RouteSingleParameter {
        private val VAULT_ID_KEY = "VAULT_ID_KEY"

        override fun navigate(id: String) {
            navController.navigateDistinct("/vault/edit/$id")
        }

        @Composable
        override fun Params(content: @Composable (id: String) -> Unit) {
            navController
                .currentBackStackEntryAsState()
                .value
                ?.arguments
                ?.getString(VAULT_ID_KEY)
                .let { rememberLastNonNull(it) }
                ?.let { content(it) }
        }

        override val route: String
            get() = "/vault/edit/{$VAULT_ID_KEY}"
    }

    val vaultViewRoute = object : RouteSingleParameter {
        private val VAULT_ID_KEY = "VAULT_ID_KEY"

        override fun navigate(id: String) {
            navController.navigateDistinct("/vault/view/$id")
        }

        @Composable
        override fun Params(content: @Composable (id: String) -> Unit) {
            navController
                .currentBackStackEntryAsState()
                .value
                ?.arguments
                ?.getString(VAULT_ID_KEY)
                .let { rememberLastNonNull(it) }
                ?.let { content(it) }
        }

        override val route: String
            get() = "/vault/view/{$VAULT_ID_KEY}"
    }
}