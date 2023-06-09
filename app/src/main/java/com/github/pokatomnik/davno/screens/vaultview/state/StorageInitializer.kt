package com.github.pokatomnik.davno.screens.vaultview.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import com.github.pokatomnik.davno.services.db.dao.vaults.Vault
import com.github.pokatomnik.davno.services.db.rememberDavnoDatabase
import com.github.pokatomnik.davno.services.storage.rememberWebdavStorageBuilder

@Composable
fun rememberVaultWebdavStorage(vaultId: Long): WebdavStorage? {
    val vaultsDAO = rememberDavnoDatabase().vaultsDAO()
    val webdavStorageBuilder = rememberWebdavStorageBuilder()

    val vaultState = remember(vaultId) {
        mutableStateOf<Vault?>(null)
    }.apply {
        LaunchedEffect(vaultId) {
            value = vaultsDAO.getOneById(vaultId)
        }
    }

    val webdavStorage = remember(vaultState.value, webdavStorageBuilder) {
        vaultState.value?.let { vault ->
            webdavStorageBuilder.build(
                userName = vault.userName,
                password = vault.password,
                rootPath = vault.rootUrl
            )
        }
    }

    return webdavStorage
}