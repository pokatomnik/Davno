package com.github.pokatomnik.davno.navigation

import androidx.compose.runtime.Composable

interface Route {
    val route: String
}

/**
 * Nonparametrized route.
 * Usually used for displaying main screen routes.
 */
interface RouteNoParameters : Route {
    /**
     * @return true if the current route is this route.
     */
    @Composable
    fun on(): Boolean

    /**
     * Method for navigation to this route
     */
    fun navigate()

    /**
     * Composable arguments extractor.
     * Technically, this method is only for consistency,
     * because the this route has no parameters
     */
    @Composable
    fun Params(content: @Composable () -> Unit)
}

interface RouteSingleParameter : Route {
    /**
     * Method for navigation to this route.
     * Single argument is required.
     */
    fun navigate(parameter0: String)

    /**
     * Composable arguments extractor.
     * Provides the single one argument.
     */
    @Composable
    fun Params(content: @Composable (parameter0: String) -> Unit)
}

interface RouteTwoParameters : Route {
    /**
     * Method for navigation to this route.
     * Two arguments required.
     */
    fun navigate(parameter0: String, parameter1: String)

    /**
     * Composable arguments extractor.
     * Provides two arguments
     */
    @Composable
    fun Params(
        content: @Composable (
            parameter0: String,
            parameter1: String,
        ) -> Unit
    )
}

interface RouteThreeParameters : Route {
    /**
     * Method for navigation to this route.
     * Three arguments required.
     */
    fun navigate(parameter0: String, parameter1: String, parameter2: String)

    /**
     * Composable arguments extractor.
     * Provides three arguments.
     */
    @Composable
    fun Params(
        content: @Composable (
            parameter0: String,
            parameter1: String,
            parameter2: String
        ) -> Unit
    )
}