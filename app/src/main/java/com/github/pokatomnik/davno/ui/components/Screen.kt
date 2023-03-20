package com.github.pokatomnik.davno.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable as AccompanistComposable


@OptIn(ExperimentalAnimationApi::class)
internal fun NavGraphBuilder.screen(
    route: String,
    keepScreenOn: Boolean = false,
    content: @Composable (AnimatedVisibilityScope.(NavBackStackEntry) -> Unit)
) {
    AccompanistComposable(
        route = route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        popEnterTransition = { fadeIn() },
        popExitTransition = { fadeOut() },
        content = { navBackStackEntry ->
            val activity = LocalContext.current as? Activity
            DisposableEffect(Unit) {
                if (keepScreenOn) {
                    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
                onDispose {
                    if (keepScreenOn) {
                        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    }
                }
            }
            content(navBackStackEntry)
        },
    )
}