package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.runtime.Composable
import com.github.pokatomnik.davno.screens.vaultview.state.rememberVaultWebdavStorage
import com.github.pokatomnik.davno.services.clipboard.rememberClipboards

@Composable
fun VaultViewMain(
    vaultId: Long,
    vaultLocation: String,
    onNavigateToVaultLocation: (vaultLocation: String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val webdavStorage = rememberVaultWebdavStorage(vaultId = vaultId)
    val clipboard = rememberClipboards().clipboardFor(vaultId)

    webdavStorage?.let {
        VaultStorageView(
            vaultLocation = vaultLocation,
            onNavigateToVaultLocation = onNavigateToVaultLocation,
            webdavStorage = webdavStorage,
            clipboard = clipboard,
            onNavigateBack = onNavigateBack
        )
    }
}