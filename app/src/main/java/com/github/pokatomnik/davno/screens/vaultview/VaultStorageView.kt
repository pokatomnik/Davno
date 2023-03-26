package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.runtime.*
import com.github.pokatomnik.davno.screens.vaultview.directory.DirectoryLister
import com.github.pokatomnik.davno.screens.vaultview.file.FileOpener
import com.github.pokatomnik.davno.screens.vaultview.state.DavResourceState
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import com.github.pokatomnik.davno.services.storage.joinPaths
import com.github.pokatomnik.davno.ui.components.makeToast
import com.thegrizzlylabs.sardineandroid.DavResource
import kotlinx.coroutines.launch

@Composable
fun VaultStorageView(
    vaultLocation: String,
    onNavigateToVaultLocation: (vaultLocation: String) -> Unit,
    webdavStorage: WebdavStorage,
    onNavigateBack: () -> Unit,
) {
    val toast = makeToast()
    val coroutineScope = rememberCoroutineScope()
    val directoryListState = remember {
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
                val davResources = webdavStorage.list(vaultLocation)
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

    LaunchedEffect(webdavStorage, vaultLocation) {
        reloadDavResources()
    }

    val handleFilePress: (davResource: DavResource) -> Unit = { davResource ->
        selectedDavResourceState.value = davResource
    }

    val handleCreateFolder: (name: String) -> Unit = { name ->
        val absoluteDirectoryPath = joinPaths(vaultLocation, name)
        coroutineScope.launch {
            try {
                webdavStorage.createDirectory(absoluteDirectoryPath)
                toast("Папка $name успешно создана")
                reloadDavResources()
            } catch (e : Exception) {
                val errorMessage = e.message ?: "Неизвестная ошибка"
                toast("Не удалось создать папку. Ошибка: $errorMessage")
            }
        }
    }

    val handleCreateFile: (name: String) -> Unit = { name ->
        val absoluteFilePath = joinPaths(vaultLocation, "$name.md")
        coroutineScope.launch {
            try {
                webdavStorage.putFile(absoluteFilePath, "")
                toast("Файл $name успешно создан")
                reloadDavResources()
            } catch (e : Exception) {
                val errorMessage = e.message ?: "Неизвестная ошибка"
                toast("Не удалось создать файл. Ошибка: $errorMessage")
            }
        }
    }

    val handleRemoveDavResource: (davResourcePath: DavResource) -> Unit = { davResource ->
        val message = if (davResource.isDirectory) {
            "Папка ${davResource.name} успешно удалена"
        } else {
            "ФАйл ${davResource.name} успешно удален"
        }
        coroutineScope.launch {
            try {
                webdavStorage.delete(davResource.path)
                toast(message)
                reloadDavResources()
            } catch (e : Exception) {
                val errorMessage = e.message ?: "Неизвестная ошибка"
                toast("Не удалось удалить. Ошибка: $errorMessage")
            }
        }
    }

    when (val selectedDavResource = selectedDavResourceState.value) {
        null -> DirectoryLister(
            vaultLocation = vaultLocation,
            onNavigateToVaultLocation = onNavigateToVaultLocation,
            directoryListState = directoryListState,
            onNavigateBack = onNavigateBack,
            onReload = reloadDavResources,
            onFilePress = handleFilePress,
            onCreateFolder = handleCreateFolder,
            onCreateFile = handleCreateFile,
            onRemoveDavResource = handleRemoveDavResource
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
