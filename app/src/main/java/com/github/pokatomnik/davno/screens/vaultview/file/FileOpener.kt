package com.github.pokatomnik.davno.screens.vaultview.file

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import com.github.pokatomnik.davno.screens.vaultview.state.DavResourceState
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import kotlinx.coroutines.launch

enum class ViewMode {
    View,
    Edit
}

@Composable
fun FileOpener(
    path: String,
    fileName: String,
    webdavStorage: WebdavStorage,
    onExit: () -> Unit,
    onSave: (content: String, onDone: () -> Unit) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val modeState = remember { mutableStateOf(ViewMode.View) }
    val contentState = remember {
        mutableStateOf(
            DavResourceState(
                isLoading = false,
                errorText = null,
                data = ""
            )
        )
    }

    val reloadDavResourceText: () -> Unit = {
        coroutineScope.launch {
            contentState.value = DavResourceState(
                isLoading = true,
                errorText = null,
                data = contentState.value.data
            )
            contentState.value = try {
                val content = webdavStorage.getFileContents(path)
                DavResourceState(
                    isLoading = false,
                    errorText = null,
                    data = content
                )
            } catch (e: Exception) {
                DavResourceState(
                    isLoading = false,
                    errorText = e.message ?: "Неизвестная ошибка загрузки",
                    data = ""
                )
            }
        }
    }

    LaunchedEffect(path, webdavStorage) {
        reloadDavResourceText()
    }

    BackHandler(enabled = true, onBack = onExit)

    when (modeState.value) {
        ViewMode.View -> FileView(
            fileName = fileName,
            isLoading = contentState.value.isLoading,
            contentMarkdown = contentState.value.data,
            reload = reloadDavResourceText,
            onEnableEdit = { modeState.value = ViewMode.Edit },
            onExit = onExit,
        )
        ViewMode.Edit -> FileEdit(
            fileName = fileName,
            isLoading = contentState.value.isLoading,
            contentMarkdownInitial = contentState.value.data,
            onSave = { editedContent ->
                modeState.value = ViewMode.View
                onSave(editedContent, reloadDavResourceText)
            },
            onExit = onExit,
        )
    }
}