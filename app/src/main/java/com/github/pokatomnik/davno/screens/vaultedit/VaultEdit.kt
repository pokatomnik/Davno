package com.github.pokatomnik.davno.screens.vaultedit

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import com.github.pokatomnik.davno.services.db.dao.vaults.Vault
import com.github.pokatomnik.davno.services.db.rememberDavnoDatabase
import com.github.pokatomnik.davno.ui.components.PageContainer
import com.github.pokatomnik.davno.ui.components.PageTitle
import kotlinx.coroutines.launch

@Composable
fun VaultEdit(
    vaultId: Long,
    onNavigateBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val vaultsDAO = rememberDavnoDatabase().vaultsDAO()

    val vaultState = remember {
        mutableStateOf<Vault?>(null)
    }

    LaunchedEffect(vaultId) {
        vaultState.value = vaultsDAO.getOneById(vaultId)
    }

    val vault = vaultState.value ?: return

    PageContainer(
        priorButton = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
        },
        header = {
            PageTitle(title = "Изменить")
        }
    ) {
        VaultEditView(
            initialVaultData = vault,
            onVaultSave = {
                name,
                userName,
                rootUrl,
                password ->

                coroutineScope.launch {
                    vaultsDAO.editOneById(
                        id = vaultId,
                        name = name,
                        rootUrl = rootUrl,
                        userName = userName,
                        password = password,
                    )
                    onNavigateBack()
                }
            }
        )
    }
}