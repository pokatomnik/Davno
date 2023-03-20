package com.github.pokatomnik.davno.screens.vaultselector

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.pokatomnik.davno.services.db.dao.vaults.Vault
import com.github.pokatomnik.davno.ui.components.ContextMenu
import com.github.pokatomnik.davno.ui.components.ContextMenuItem
import com.github.pokatomnik.davno.ui.components.LazyList
import com.github.pokatomnik.davno.ui.components.ListNavItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun VaultSelectorView(
    list: List<Vault>,
    onNavigateToVault: (item: Vault) -> Unit,
    onEditVault: (item: Vault) -> Unit,
    onVaultRemove: (item: Vault) -> Unit,
    onVaultDuplicate: (item: Vault) -> Unit,
    onRefresh: () -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = false),
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
        content = {
            LazyList(
                list = list,
                renderItem = { _, vaultItem ->
                    val contextMenuDisplayedState = remember { mutableStateOf(false) }

                    ContextMenu(
                        expanded = contextMenuDisplayedState.value,
                        onDismiss = { contextMenuDisplayedState.value = false },
                        items = listOf(
                            object : ContextMenuItem {
                                override val id = "ID_EDIT"
                                override val title = "Редактировать"
                                override fun onClick(id: String) {
                                    onEditVault(vaultItem)
                                }
                            },
                            object : ContextMenuItem {
                                override val id = "ID_DUPLICATE"
                                override val title = "Дублировать"
                                override fun onClick(id: String) {
                                    onVaultDuplicate(vaultItem)
                                }
                            },
                            object : ContextMenuItem {
                                override val id = "ID_REMOVE"
                                override val title = "Удалить"
                                override fun onClick(id: String) {
                                    onVaultRemove(vaultItem)
                                }
                            }
                        ),
                        content = {
                            ListNavItem(
                                title = vaultItem.name,
                                onPress = { onNavigateToVault(vaultItem) },
                                onLongPress = { contextMenuDisplayedState.value = true }
                            )
                        }
                    )
                }
            )
        }
    )
}