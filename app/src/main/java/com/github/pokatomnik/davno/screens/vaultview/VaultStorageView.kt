package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.runtime.*
import com.github.pokatomnik.davno.screens.vaultview.directory.DirectoryLister
import com.github.pokatomnik.davno.screens.vaultview.file.FileOpener
import com.github.pokatomnik.davno.screens.vaultview.state.DavResourceState
import com.github.pokatomnik.davno.services.clipboard.ClipboardIntentionId
import com.github.pokatomnik.davno.services.clipboard.DavnoClipboard
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import com.github.pokatomnik.davno.services.storage.joinPaths
import com.github.pokatomnik.davno.services.usermessage.rememberMessageDisplayer
import com.thegrizzlylabs.sardineandroid.DavResource
import kotlinx.coroutines.launch

@Composable
fun VaultStorageView(
    vaultLocation: String,
    onNavigateToVaultLocation: (vaultLocation: String) -> Unit,
    webdavStorage: WebdavStorage,
    clipboard: DavnoClipboard,
    onNavigateBack: () -> Unit,
) {
    val messageDisplayer = rememberMessageDisplayer()
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
            try {
                webdavStorage.putFile(
                    relativeFilePath = filePath,
                    data = markdownContents
                )
                messageDisplayer.display("Файл $fileName успешно сохранен")
            } catch (e: Exception) {
                messageDisplayer.display("Ошибка сохранения файла $fileName")
            } finally {
                onDone()
            }
        }
    }

    val handleFilePress: (davResource: DavResource) -> Unit = { davResource ->
        selectedDavResourceState.value = davResource
    }

    val handleCreateFolder: (name: String) -> Unit = { name ->
        val absoluteDirectoryPath = joinPaths(vaultLocation, name)
        coroutineScope.launch {
            try {
                webdavStorage.createDirectory(absoluteDirectoryPath)
                messageDisplayer.display("Папка $name успешно создана")
                reloadDavResources()
            } catch (e : Exception) {
                val errorMessage = e.message ?: "Неизвестная ошибка"
                messageDisplayer.display("Не удалось создать папку. Ошибка: $errorMessage")
            }
        }
    }

    val handleCreateFile: (name: String) -> Unit = { name ->
        val absoluteFilePath = joinPaths(vaultLocation, "$name.md")
        coroutineScope.launch {
            try {
                webdavStorage.putFile(absoluteFilePath, "")
                messageDisplayer.display("Файл $name успешно создан")
                reloadDavResources()
            } catch (e : Exception) {
                val errorMessage = e.message ?: "Неизвестная ошибка"
                messageDisplayer.display("Не удалось создать файл. Ошибка: $errorMessage")
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
                messageDisplayer.display(message)
                reloadDavResources()
            } catch (e : Exception) {
                val errorMessage = e.message ?: "Неизвестная ошибка"
                messageDisplayer.display("Не удалось удалить. Ошибка: $errorMessage")
            }
        }
    }

    val handlePasteFiles: (ClipboardIntentionId, List<DavResource>) -> Unit = {
            intentionId,
            filesToPaste ->
        when (intentionId) {
            ClipboardIntentionId.Copy -> filesToPaste.forEach { fileToCopy ->
                coroutineScope.launch {
                    try {
                        webdavStorage.copyFile(
                            relativePathFrom = fileToCopy.path,
                            relativePathTo = joinPaths(vaultLocation, fileToCopy.name)
                        )
                        reloadDavResources()
                    } catch (e: Exception) {
                        messageDisplayer.display("Ошибка копирования файла")
                    }
                }
            }
            ClipboardIntentionId.Cut -> filesToPaste.forEach { fileToMove ->
                coroutineScope.launch {
                    try {
                        webdavStorage.moveFile(
                            relativePathFrom = fileToMove.path,
                            relativePathTo = joinPaths(vaultLocation, fileToMove.name)
                        )
                        reloadDavResources()
                    } catch (e: Exception) {
                        messageDisplayer.display("Ошибка перемещения файла")
                    }
                }
            }
        }
    }

    LaunchedEffect(webdavStorage, vaultLocation) {
        reloadDavResources()
    }

    when (val selectedDavResource = selectedDavResourceState.value) {
        null -> DirectoryLister(
            vaultLocation = vaultLocation,
            onNavigateToVaultLocation = onNavigateToVaultLocation,
            directoryListState = directoryListState,
            clipboard = clipboard,
            onNavigateBack = onNavigateBack,
            onReload = reloadDavResources,
            onFilePress = handleFilePress,
            onCreateFolder = handleCreateFolder,
            onCreateFile = handleCreateFile,
            onRemoveDavResource = handleRemoveDavResource,
            onPasteFiles = handlePasteFiles,
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
