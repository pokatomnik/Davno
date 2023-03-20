package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.github.pokatomnik.davno.services.db.dao.vaults.Vault
import com.github.pokatomnik.davno.services.db.rememberDavnoDatabase
import com.github.pokatomnik.davno.services.storage.ConnectionParams
import com.github.pokatomnik.davno.services.storage.rememberWebdavStorageBuilder

@Composable
fun VaultView(
    vaultId: Long,
    onNavigateBack: () -> Unit,
) {
    val vaultsDAO = rememberDavnoDatabase().vaultsDAO()
    val webdavStorageBuilder = rememberWebdavStorageBuilder()
    val vaultState = remember(vaultId) { mutableStateOf<Vault?>(null) }
    val currentPathState = remember(vaultId) {
        mutableStateOf("/")
    }
    val webdavStorage = remember(vaultState.value) {
        vaultState.value?.let { vault ->
            webdavStorageBuilder.build(
                connectionParams = ConnectionParams(
                    userName = vault.userName,
                    password = vault.password,
                    rootPath = vault.rootUrl
                )
            )
        }
    }

    LaunchedEffect(vaultId) {
        vaultState.value = vaultsDAO.getOneById(vaultId)
    }

    if (webdavStorage == null) return

    DirectoryLister(pathState = currentPathState, webdavStorage = webdavStorage, onNavigateBack = {})
}