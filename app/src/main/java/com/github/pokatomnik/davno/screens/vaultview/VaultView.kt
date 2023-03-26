package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun VaultView(
    vaultId: Long,
    vaultLocation: String,
    onNavigateToVaultLocation: (vaultLocation: String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val webdavStorage = rememberLocalWebdavStorage(vaultId = vaultId)

    webdavStorage?.let {
        WebdavNavigator(
            vaultLocation = vaultLocation,
            onNavigateToVaultLocation = onNavigateToVaultLocation,
            webdavStorage = webdavStorage,
            onNavigateBack = onNavigateBack
        )
    }
}