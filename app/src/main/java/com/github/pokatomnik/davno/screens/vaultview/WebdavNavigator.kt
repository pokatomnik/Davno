package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.runtime.*
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import com.thegrizzlylabs.sardineandroid.DavResource
import kotlinx.coroutines.launch

@Composable
fun WebdavNavigator(
    history: History<String>,
    webdavStorage: WebdavStorage,
    onNavigateBackToVaultSelector: () -> Unit,
) {
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
    val fileViewPathState = remember {
        mutableStateOf<String?>(null)
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
        fileViewPathState.value = davResource.path
    }

    when (val fileViewPath = fileViewPathState.value) {
        null -> DirectoryView(
            history = history,
            directoryListState = directoryListState,
            onNavigateBack = handleNavigateBack,
            onReload = reloadDavResources,
            onFilePress = handleFilePress
        )
        else -> FileView(
            path = fileViewPath,
            webdavStorage = webdavStorage,
            onExit = { fileViewPathState.value = null }
        )
    }

}
