package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun VaultView(
    vaultId: Long,
    onNavigateBackToVaultSelector: () -> Unit,
) {
    val webdavStorage = rememberLocalWebdavStorage(vaultId = vaultId)
    val currentHistoryState = rememberHistory(initialValue = "/")

    webdavStorage?.let {
        WebdavNavigator(
            history = currentHistoryState,
            webdavStorage = webdavStorage,
            onNavigateBackToVaultSelector = onNavigateBackToVaultSelector
        )
    }
}