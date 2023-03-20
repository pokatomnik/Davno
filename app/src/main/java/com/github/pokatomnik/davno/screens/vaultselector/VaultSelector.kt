package com.github.pokatomnik.davno.screens.vaultselector

import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.pokatomnik.davno.services.db.dao.vaults.Vault
import com.github.pokatomnik.davno.services.db.rememberDavnoDatabase
import com.github.pokatomnik.davno.ui.components.LARGE_PADDING
import com.github.pokatomnik.davno.ui.components.PageContainer
import com.github.pokatomnik.davno.ui.components.PageTitle
import kotlinx.coroutines.launch

@Composable
fun VaultSelector(
    onNavigateToVaultAdd: () -> Unit,
    onNavigateToVault: (vaultId: Long) -> Unit,
    onNavigateToVaultEdit: (vaultId: Long) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val vaultsDAO = rememberDavnoDatabase().vaultsDAO()
    val vaultListState = remember { mutableStateOf<List<Vault>?>(null) }
    val refreshList: () -> Unit = {
        coroutineScope.launch {
            vaultListState.value = vaultsDAO.getAll()
        }
    }
    val removeVaultAndRefresh: (id: Long) -> Unit = { id ->
        coroutineScope.launch {
            vaultsDAO.deleteById(id)
            vaultListState.value = vaultsDAO.getAll()
        }
    }
    val duplicateVaultAndRefresh: (id: Long) -> Unit = { id ->
        coroutineScope.launch {
            vaultsDAO.duplicateById(id)
            vaultListState.value = vaultsDAO.getAll()
        }
    }

    LaunchedEffect(Unit) {
        refreshList()
    }

    PageContainer(
        header = {
            PageTitle(title = "Хранилища")
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            VaultSelectorView(
                list = vaultListState.value ?: listOf(),
                onNavigateToVault = { vault ->
                    onNavigateToVault(vault.id)
                },
                onEditVault = { vault ->
                    onNavigateToVaultEdit(vault.id)
                },
                onVaultRemove = { vault ->
                    removeVaultAndRefresh(vault.id)
                },
                onVaultDuplicate = { vault ->
                    duplicateVaultAndRefresh(vault.id)
                },
                onRefresh = refreshList
            )
            Row(
                modifier = Modifier
                    .padding(all = (LARGE_PADDING * 2).dp)
                    .align(Alignment.BottomEnd)
            ) {
                FloatingActionButton(
                    onClick = onNavigateToVaultAdd,
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Добавить конфигурацию"
                        )
                    },
                    contentColor = MaterialTheme.colors.onPrimary
                )
            }

        }
    }
}