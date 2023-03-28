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
import com.github.pokatomnik.davno.services.storage.lastPathPartOrEmpty
import com.github.pokatomnik.davno.services.storage.up
import com.github.pokatomnik.davno.services.usermessage.rememberMessageDisplayer
import com.github.pokatomnik.davno.ui.components.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.thegrizzlylabs.sardineandroid.DavResource

@Composable
fun DirectoryLister(
    vaultLocation: String,
    directoryListState: MutableState<DavResourceState<List<DavResource>>>,
    clipboard: DavnoClipboard,
    onNavigateToVaultLocation: (vaultLocation: String) -> Unit,
    onNavigateBack: () -> Unit,
    onReload: () -> Unit,
    onFilePress: (file: DavResource) -> Unit,
    onCreateFolder: (name: String) -> Unit,
    onCreateFile: (name: String) -> Unit,
    onRemoveDavResource: (davResource: DavResource) -> Unit,
    onPasteFiles: (
        intention: ClipboardIntentionId,
        filesToPaste: List<DavResource>
    ) -> Unit,
) {
    val messageDisplayer = rememberMessageDisplayer()
    val folderCreationNameConfirmationVisibilityState = remember { mutableStateOf(false) }
    val fileCreationNameConfirmationVisibilityState = remember { mutableStateOf(false) }

    val handlePasteFilesHere: () -> Unit = {
        clipboard.paste { intentionId, filesToPaste ->
            onPasteFiles(intentionId, filesToPaste)
        }
    }

    val handleCopyFiles: (List<DavResource>) -> Unit = { davResources ->
        clipboard.copy(davResources)
    }

    val handleCutFiles: (List<DavResource>) -> Unit = { davResources ->
        clipboard.cut(davResources)
    }

    CreateDavResourceDialog(
        title = { Text(text = "Имя новой папки", fontWeight = FontWeight.Bold) },
        visibilityState = folderCreationNameConfirmationVisibilityState,
        onNameValidationFailed = { messageDisplayer.display("Введите корректное имя папки") },
        onCreateRequest = onCreateFolder
    )
    CreateDavResourceDialog(
        title = { Text(text = "Имя нового файла", fontWeight = FontWeight.Bold) },
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
                        get() = "Вставить файл"
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
            LazyList(list = directoryListState.value.data, renderItem = { index, davResource ->
                val contextMenuDisplayedState = remember { mutableStateOf(false) }
                when (index) {
                    0 -> if (vaultLocation == "/") return@LazyList else IconicListNavItem(title = "..",
                        icon = Icons.Filled.Folder,
                        iconContentDescription = "Папка \"${davResource.path.up()}\"",
                        onPress = {
                            onNavigateToVaultLocation(davResource.path.up())
                        })
                    else -> ContextMenu(expanded = contextMenuDisplayedState.value,
                        items = (if (davResource.isDirectory) listOf<ContextMenuItem>() else listOf(
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
                            })) + listOf(object : ContextMenuItem {
                            override val id = "ID_REMOVE"
                            override val title = "Удалить"
                            override fun onClick(id: String) {
                                // TODO implement removal confirmation here
                                onRemoveDavResource(davResource)
                            }
                        }),
                        onDismiss = { contextMenuDisplayedState.value = false },
                        content = {
                            IconicListNavItem(title = davResource.name,
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
                                onLongPress = { contextMenuDisplayedState.value = true })
                        })
                }
            })
        }
    }
}