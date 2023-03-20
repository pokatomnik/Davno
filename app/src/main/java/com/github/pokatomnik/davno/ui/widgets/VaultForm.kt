package com.github.pokatomnik.davno.ui.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.github.pokatomnik.davno.ui.components.LARGE_PADDING

@Composable
fun VaultForm(
    name: String,
    onNameChange: (newName: String) -> Unit,
    rootUrl: String,
    onRootUrlChange: (newRootUrl: String) -> Unit,
    userName: String,
    onUserNameChange: (newUserName: String) -> Unit,
    password: String,
    onPasswordChange: (newPassword: String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                placeholder = {
                    Text(text = "Имя конфигурации")
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
            )
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(LARGE_PADDING.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = rootUrl,
                onValueChange = onRootUrlChange,
                placeholder = {
                    Text(text = "Адрес WebDav сервера")
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
            )
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(LARGE_PADDING.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = userName,
                onValueChange = onUserNameChange,
                placeholder = {
                    Text(text = "Имя пользователя")
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
            )
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(LARGE_PADDING.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                placeholder = {
                    Text(text = "Пароль")
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                maxLines = 1,
            )
        }
    }
}
