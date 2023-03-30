package com.github.pokatomnik.davno.screens.vaultview.directory

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import com.github.pokatomnik.davno.screens.vaultview.state.DavResourceState
import com.github.pokatomnik.davno.services.clipboard.ClipboardIntentionId
import com.github.pokatomnik.davno.services.clipboard.DavnoClipboard
import com.github.pokatomnik.davno.services.storage.DavnoDavResource
import com.github.pokatomnik.davno.services.storage.lastPathPartOrEmpty
import com.github.pokatomnik.davno.services.storage.up
import com.github.pokatomnik.davno.services.usermessage.rememberMessageDisplayer
import com.github.pokatomnik.davno.ui.components.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun DirectoryLister(
    vaultLocation: String,
    directoryListState: MutableState<DavResourceState<List<DavnoDavResource>>>,
    clipboard: DavnoClipboard,
    onNavigateToVaultLocation: (vaultLocation: String) -> Unit,
    onNavigateBack: () -> Unit,
    onReload: () -> Unit,
    onFilePress: (file: DavnoDavResource) -> Unit,
    onCreateFolder: (name: String) -> Unit,
    onCreateFile: (name: String) -> Unit,
    onRemoveDavResource: (davResource: DavnoDavResource) -> Unit,
    onPasteFiles: (
        intention: ClipboardIntentionId,
        filesToPaste: List<DavnoDavResource>
    ) -> Unit,
    onRename: (path: String, newName: String, isDirectory: Boolean) -> Unit,
) {
    val messageDisplayer = rememberMessageDisplayer()
    val folderCreationNameConfirmationVisibilityState = remember { mutableStateOf(false) }
    val fileCreationNameConfirmationVisibilityState = remember { mutableStateOf(false) }

    val handlePasteFilesHere: () -> Unit = {
        clipboard.paste { intentionId, filesToPaste ->
            onPasteFiles(intentionId, filesToPaste)
        }
    }

    val handleCopyFiles: (List<DavnoDavResource>) -> Unit = { davResources ->
        clipboard.copy(davResources)
    }

    val handleCutFiles: (List<DavnoDavResource>) -> Unit = { davResources ->
        clipboard.cut(davResources)
    }

    CreateDavResourceDialog(
        title = { Text(text = "Имя новой папки", fontWeight = FontWeight.Bold) },
        primaryButtonText = { Text(text = "Создать") },
        visibilityState = folderCreationNameConfirmationVisibilityState,
        onNameValidationFailed = { messageDisplayer.display("Введите корректное имя папки") },
        onCreateRequest = onCreateFolder
    )
    CreateDavResourceDialog(
        title = { Text(text = "Имя нового файла", fontWeight = FontWeight.Bold) },
        primaryButtonText = { Text(text = "Создать") },
        visibilityState = fileCreationNameConfirmationVisibilityState,
        onNameValidationFailed = { messageDisplayer.display("Введите корректное имя файла") },
        onCreateRequest = onCreateFile
    )
    PageContainer(priorButton = {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Filled.ArrowBack, contentDescription = "Назад"
            )
        }
    }, header = {
        PageTitle(title = vaultLocation.lastPathPartOrEmpty().ifEmpty { "/" })
    }, trailingButton = {
        val menuDisplayedState = remember { mutableStateOf(false) }
        ContextMenu(expanded = menuDisplayedState.value,
            items = (if (clipboard.empty.collectAsState().value) listOf<ContextMenuItem>() else listOf(
                object : ContextMenuItem {
                    override val id: String
                        get() = "ID_PASTE_FROM_CLIPBOARD"
                    override val title: String
                        get() = "Вставить"

                    override fun onClick(id: String) {
                        handlePasteFilesHere()
                    }
                })) + listOf(object : ContextMenuItem {
                override val id = "ID_CREATE_DIRECTORY"
                override val title = "Создать папку"
                override fun onClick(id: String) {
                    folderCreationNameConfirmationVisibilityState.value = true
                }
            }, object : ContextMenuItem {
                override val id = "ID_CREATE_FILE"
                override val title = "Создать файл"
                override fun onClick(id: String) {
                    fileCreationNameConfirmationVisibilityState.value = true
                }
            }),
            onDismiss = { menuDisplayedState.value = false },
            content = {
                IconButton(onClick = { menuDisplayedState.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert, contentDescription = "Меню"
                    )
                }
            })
    }) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(
                isRefreshing = directoryListState.value.isLoading
            ), onRefresh = onReload
        ) {
            val list = if (vaultLocation == "/") {
                directoryListState.value.data
            } else {
                listOf<DavnoDavResource?>(null) + directoryListState.value.data
            }
            LazyList(
                list = list,
                renderItem = { _, davResource ->
                    val contextMenuDisplayedState = remember { mutableStateOf(false) }
                    if (davResource == null) {
                        IconicListNavItem(title = "..",
                            icon = Icons.Filled.Folder,
                            iconContentDescription = "Вверх",
                            onPress = {
                                onNavigateToVaultLocation(vaultLocation.up())
                            }
                        )
                    } else {
                        val renameDisplayedState = remember { mutableStateOf(false) }

                        CreateDavResourceDialog(
                            title = { Text(text = "Новое имя", fontWeight = FontWeight.Bold) },
                            primaryButtonText = { Text(text = "Переименовать") },
                            visibilityState = renameDisplayedState,
                            onNameValidationFailed = { messageDisplayer.display("Введите корректное имя") },
                            onCreateRequest = { newName ->
                                onRename(davResource.path, newName, davResource.isDirectory)
                            }
                        )

                        ContextMenu(expanded = contextMenuDisplayedState.value,
                            items = listOf(
                                object : ContextMenuItem {
                                    override val id = "ID_CUT"
                                    override val title = "Вырезать"
                                    override fun onClick(id: String) {
                                        handleCutFiles(listOf(davResource))
                                    }
                                },
                                object : ContextMenuItem {
                                    override val id = "ID_COPY"
                                    override val title = "Копировать"
                                    override fun onClick(id: String) {
                                        handleCopyFiles(listOf(davResource))
                                    }
                                },
                                object : ContextMenuItem {
                                    override val id = "ID_RENAME"
                                    override val title = "Переименовать"
                                    override fun onClick(id: String) {
                                        renameDisplayedState.value = true
                                    }
                                },
                                object : ContextMenuItem {
                                    override val id = "ID_REMOVE"
                                    override val title = "Удалить"
                                    override fun onClick(id: String) {
                                        // TODO implement removal confirmation here
                                        onRemoveDavResource(davResource)
                                    }
                                }
                            ),
                            onDismiss = { contextMenuDisplayedState.value = false },
                            content = {
                                IconicListNavItem(
                                    title = davResource.name,
                                    icon = when (davResource.isDirectory) {
                                        true -> Icons.Filled.Folder
                                        false -> Icons.Filled.Description
                                    },
                                    iconContentDescription = when (davResource.isDirectory) {
                                        true -> "Папка \"${davResource.name}\""
                                        false -> "Файл \"${davResource.name}\""
                                    },
                                    onPress = {
                                        if (davResource.isDirectory) {
                                            onNavigateToVaultLocation(davResource.path)
                                        } else {
                                            onFilePress(davResource)
                                        }
                                    },
                                    onLongPress = { contextMenuDisplayedState.value = true }
                                )
                            }
                        )
                    }
                }
            )
        }
    }
}