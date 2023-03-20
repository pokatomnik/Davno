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

    val nameState = remember(initialVaultData.name) {
        mutableStateOf(initialVaultData.name)
    }
    val rootUrlState = remember(initialVaultData.rootUrl) {
        mutableStateOf(initialVaultData.rootUrl)
    }
    val userNameState = remember(initialVaultData.userName) {
        mutableStateOf(initialVaultData.userName)
    }
    val passwordState = remember(initialVaultData.password) {
        mutableStateOf(initialVaultData.password)
    }

    val handleSave: () -> Unit = {
        val areSomeBlank = nameState.value.isBlank() ||
                rootUrlState.value.isBlank() ||
                userNameState.value.isBlank() ||
                passwordState.value.isBlank()
        if (areSomeBlank) {
            toast("Заполните поля")
        } else {
            onVaultSave(
                nameState.value,
                userNameState.value,
                rootUrlState.value,
                passwordState.value
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = LARGE_PADDING.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height((LARGE_PADDING * 2).dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Конфигурация подключения")
            }
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(LARGE_PADDING.dp))
            VaultForm(
                name = nameState.value,
                onNameChange = { nameState.value = it },
                rootUrl = rootUrlState.value,
                onRootUrlChange = { rootUrlState.value = it },
                userName = userNameState.value,
                onUserNameChange = { userNameState.value = it },
                password = passwordState.value,
                onPasswordChange = { passwordState.value = it }
            )
        }
        Row(
            modifier = Modifier
                .padding(all = (LARGE_PADDING * 2).dp)
                .align(Alignment.BottomEnd)
        ) {
            FloatingActionButton(
                onClick = handleSave,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Сохранить"
                    )
                },
                contentColor = MaterialTheme.colors.onPrimary
            )
        }

    }
}