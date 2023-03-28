package com.github.pokatomnik.davno.screens.vaultadd

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.pokatomnik.davno.services.db.rememberDavnoDatabase
import com.github.pokatomnik.davno.services.usermessage.rememberMessageDisplayer
import com.github.pokatomnik.davno.ui.components.LARGE_PADDING
import com.github.pokatomnik.davno.ui.components.PageContainer
import com.github.pokatomnik.davno.ui.components.PageTitle
import com.github.pokatomnik.davno.ui.widgets.VaultForm
import com.github.pokatomnik.davno.ui.widgets.rememberWebdavConnectionTester
import kotlinx.coroutines.launch

@Composable
fun VaultAdd(
    onNavigateBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val vaultsDAO = rememberDavnoDatabase().vaultsDAO()
    val messageDisplayer = rememberMessageDisplayer()
    val webdavConnectionTester = rememberWebdavConnectionTester()

    val nameState = remember { mutableStateOf("") }
    val rootUrlState = remember { mutableStateOf("") }
    val userNameState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    val handleSave: () -> Unit = {
        val areSomeBlank = nameState.value.isBlank() ||
                rootUrlState.value.isBlank() ||
                userNameState.value.isBlank() ||
                passwordState.value.isBlank()
        if (areSomeBlank) {
            messageDisplayer.display("Заполните поля")
        } else coroutineScope.launch {
            vaultsDAO.add(
                name = nameState.value,
                rootUrl = rootUrlState.value,
                userName = userNameState.value,
                password = passwordState.value
            )
            onNavigateBack()
        }
    }

    val testConnection: () -> Unit = {
        webdavConnectionTester(
            userNameState.value,
            passwordState.value,
            rootUrlState.value
        ).apply {
            onOk { messageDisplayer.display("Соединение установлено") }
            onFail { messageDisplayer.display("Не удалось установить соединение") }
            test()
        }
    }

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
            PageTitle(title = "Добавить")
        },
        trailingButton = {
            IconButton(onClick = testConnection) {
                Icon(
                    imageVector = Icons.Filled.BugReport,
                    contentDescription = "Проверить"
                )
            }
        }
    ) {
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
}