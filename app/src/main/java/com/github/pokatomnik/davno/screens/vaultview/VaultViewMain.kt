package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.runtime.Composable
import com.github.pokatomnik.davno.screens.vaultview.state.rememberVaultWebdavStorage

@Composable
fun VaultViewMain(
    vaultId: Long,
    vaultLocation: String,
    onNavigateToVaultLocation: (vaultLocation: String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val webdavStorage = rememberVaultWebdavStorage(vaultId = vaultId)

    webdavStorage?.let {
        VaultStorageView(
            vaultLocation = vaultLocation,
            onNavigateToVaultLocation = onNavigateToVaultLocation,
            webdavStorage = webdavStorage,
            onNavigateBack = onNavigateBack
        )
    }
}