package com.github.pokatomnik.davno

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.pokatomnik.davno.navigation.rememberNavigation
import com.github.pokatomnik.davno.screens.vaultadd.VaultAdd
import com.github.pokatomnik.davno.screens.vaultedit.VaultEdit
import com.github.pokatomnik.davno.screens.vaultselector.VaultSelector
import com.github.pokatomnik.davno.screens.vaultview.VaultView
import com.github.pokatomnik.davno.ui.components.screen
import com.github.pokatomnik.davno.ui.widgets.DavnoBottomNavigation
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppComposable() {
    val navigation = rememberNavigation()
    Scaffold(
        content = { scaffoldPaddingValues ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                AnimatedNavHost(
                    navController = navigation.navController,
                    startDestination = navigation.defaultRoute.route,
                    modifier = Modifier.padding(scaffoldPaddingValues)
                ) {
                    screen(route = navigation.vaultSelectorRoute.route) {
                        navigation.vaultSelectorRoute.Params {
                            VaultSelector(
                                onNavigateToVault = { vaultId ->
                                    navigation.vaultViewRoute.navigate(vaultId.toString())
                                },
                                onNavigateToVaultAdd = {
                                    navigation.vaultAddRoute.navigate()
                                },
                                onNavigateToVaultEdit = { vaultId ->
                                    navigation.vaultEditRoute.navigate(vaultId.toString())
                                }
                            )
                        }
                    }
                    screen(route = navigation.vaultAddRoute.route) {
                        navigation.vaultAddRoute.Params {
                            VaultAdd(
                                onNavigateBack = {
                                    navigation.navigateBack()
                                }
                            )
                        }
                    }
                    screen(route = navigation.vaultEditRoute.route) {
                        navigation.vaultEditRoute.Params { vaultId ->
                            VaultEdit(
                                vaultId = vaultId.toLong(),
                                onNavigateBack = {
                                    navigation.navigateBack()
                                }
                            )
                        }
                    }
                    screen(route = navigation.vaultViewRoute.route) {
                        navigation.vaultViewRoute.Params { vaultId ->
                            VaultView(
                                vaultId = vaultId.toLong(),
                                onNavigateBack = {
                                    navigation.navigateBack()
                                }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = { DavnoBottomNavigation(navigation = navigation) }
    )
}