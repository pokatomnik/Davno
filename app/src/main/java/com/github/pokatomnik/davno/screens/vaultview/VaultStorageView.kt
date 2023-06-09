package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.runtime.*
import com.github.pokatomnik.davno.screens.vaultview.directory.DirectoryLister
import com.github.pokatomnik.davno.screens.vaultview.file.FileOpener
import com.github.pokatomnik.davno.screens.vaultview.state.DavResourceState
import com.github.pokatomnik.davno.services.clipboard.ClipboardIntentionId
import com.github.pokatomnik.davno.services.clipboard.DavnoClipboard
import com.github.pokatomnik.davno.services.storage.DavnoDavResource
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import com.github.pokatomnik.davno.services.storage.ensureHasExtension
import com.github.pokatomnik.davno.services.storage.joinPaths
import com.github.pokatomnik.davno.services.usermessage.rememberMessageDisplayer
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
            DavResourceState<List<DavnoDavResource>>(
                isLoading = false,
                errorText = null,
                data = listOf()
            )
        )
    }
    val selectedDavResourceState = remember {
        mutableStateOf<DavnoDavResource?>(null)
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
                    path = filePath,
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

    val handleFilePress: (davResource: DavnoDavResource) -> Unit = { davResource ->
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
        val absoluteFilePath = joinPaths(vaultLocation, name.ensureHasExtension("md"))
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

    val handleRemoveDavResource: (davResourcePath: DavnoDavResource) -> Unit = { davResource ->
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

    val handlePasteFiles: (ClipboardIntentionId, List<DavnoDavResource>) -> Unit = {
            intentionId,
            filesToPaste ->
        when (intentionId) {
            ClipboardIntentionId.Copy -> filesToPaste.forEach { fileToCopy ->
                coroutineScope.launch {
                    try {
                        webdavStorage.copy(
                            fromPath = fileToCopy.path,
                            toPath = joinPaths(vaultLocation, fileToCopy.name)
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
                        webdavStorage.move(
                            fromPath = fileToMove.path,
                            toPath = joinPaths(vaultLocation, fileToMove.name)
                        )
                        reloadDavResources()
                    } catch (e: Exception) {
                        messageDisplayer.display("Ошибка перемещения файла")
                    }
                }
            }
        }
    }

    val handleRename: (String, String, Boolean) -> Unit = { path, newName, isDirectory ->
        val actualName = if (isDirectory) newName else newName.ensureHasExtension("md")
        coroutineScope.launch {
            try {
                webdavStorage.rename(path, actualName)
                reloadDavResources()
            } catch (e: Exception) {
                messageDisplayer.display("Ошибка переименования")
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
            onRename = handleRename
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
