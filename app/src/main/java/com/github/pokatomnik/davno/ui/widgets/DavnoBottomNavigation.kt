package com.github.pokatomnik.davno.ui.widgets

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.github.pokatomnik.davno.navigation.Navigation

@Composable
fun DavnoBottomNavigation(navigation: Navigation) {
    BottomNavigation(elevation = 1.dp) {
        BottomNavigationItem(
            selected = navigation.vaultSelectorRoute.on() || navigation.vaultAddRoute.on(),
            onClick = { navigation.vaultSelectorRoute.navigate() },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Главная"
                )
            }
        )
        BottomNavigationItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Настройки"
                )
            }
        )
    }
}