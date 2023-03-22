package com.github.pokatomnik.davno.screens.vaultedit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.pokatomnik.davno.services.db.dao.vaults.Vault
import com.github.pokatomnik.davno.ui.components.LARGE_PADDING
import com.github.pokatomnik.davno.ui.components.makeToast
import com.github.pokatomnik.davno.ui.widgets.VaultForm

@Composable
fun VaultEditView(
    initialVaultData: Vault,
    onVaultSave: (
        name: String,
        userName: String,
        rootUrl: String,
        password: String
    ) -> Unit,
) {
    val toast = makeToast()


}