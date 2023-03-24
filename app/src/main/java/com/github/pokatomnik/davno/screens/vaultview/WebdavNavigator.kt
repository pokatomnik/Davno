package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.runtime.*
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import com.github.pokatomnik.davno.ui.components.makeToast
import com.thegrizzlylabs.sardineandroid.DavResource
import kotlinx.coroutines.launch

@Composable
fun WebdavNavigator(
    history: History<String>,
    webdavStorage: WebdavStorage,
    onNavigateBackToVaultSelector: () -> Unit,
) {
    val toast = makeToast()
    val coroutineScope = rememberCoroutineScope()
    val directoryListState = remember(history.currentValue, webdavStorage) {
        mutableStateOf(
            DavResourceState<List<DavResource>>(
                isLoading = false,
                errorText = null,
                data = listOf()
            )
        )
    }
    val selectedDavResourceState = remember {
        mutableStateOf<DavResource?>(null)
    }

    val reloadDavResources: () -> Unit = {
        coroutineScope.launch {
            directoryListState.value = DavResourceState(
                isLoading = true,
                errorText = null,
                data = directoryListState.value.data
            )
            directoryListState.value = try {
                val davResources = webdavStorage.list(history.currentValue)
                DavResourceState(
                    isLoading = false,
                    errorText = null,
                    data = davResources
                )
            } catch (e: Exception) {
                DavResourceState(
                    isLoading = false,
                    errorText = e.message ?: "Неизвестная ошибка загрузки",
                    data = listOf()
                )
            }
        }
    }
    val saveFile: (
        fileName: String,
        filePath: String,
        markdownContents: String,
        onDone: () -> Unit
    ) -> Unit = {
        fileName,
        filePath,
        markdownContents,
        onDone ->

        coroutineScope.launch {
            webdavStorage.putFile(
                relativeFilePath = filePath,
                data = markdownContents
            )
            onDone()
            toast("Файл $fileName успешно сохранен")
        }
    }

    LaunchedEffect(webdavStorage, history.currentValue) {
        reloadDavResources()
    }

    val handleNavigateBack: () -> Unit = {
        if (history.canGoBackward) {
            history.moveBackward()
        } else {
            onNavigateBackToVaultSelector()
        }
    }

    val handleFilePress: (davResource: DavResource) -> Unit = { davResource ->
        selectedDavResourceState.value = davResource
    }

    when (val selectedDavResource = selectedDavResourceState.value) {
        null -> DirectoryView(
            history = history,
            directoryListState = directoryListState,
            onNavigateBack = handleNavigateBack,
            onReload = reloadDavResources,
            onFilePress = handleFilePress
        )
        else -> FileOpener(
            path = selectedDavResource.path,
            fileName = selectedDavResource.name,
            webdavStorage = webdavStorage,
            onExit = { selectedDavResourceState.value = null },
            onSave = { editedMarkdownContents, onDone ->
                saveFile(
                    selectedDavResource.name,
                    selectedDavResource.path,
                    editedMarkdownContents,
                    onDone,
                )
            }
        )
    }

}
