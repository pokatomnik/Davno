package com.github.pokatomnik.davno.screens.vaultview.directory

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import com.github.pokatomnik.davno.screens.vaultview.state.DavResourceState
import com.github.pokatomnik.davno.services.storage.lastPathPartOrEmpty
import com.github.pokatomnik.davno.services.storage.up
import com.github.pokatomnik.davno.ui.components.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.thegrizzlylabs.sardineandroid.DavResource

@Composable
fun DirectoryLister(
    vaultLocation: String,
    directoryListState: MutableState<DavResourceState<List<DavResource>>>,
    onNavigateToVaultLocation: (vaultLocation: String) -> Unit,
    onNavigateBack: () -> Unit,
    onReload: () -> Unit,
    onFilePress: (file: DavResource) -> Unit,
    onCreateFolder: (name: String) -> Unit,
    onCreateFile: (name: String) -> Unit,
    onRemoveDavResource: (davResource: DavResource) -> Unit,
) {
    val toast = makeToast()
    val folderCreationNameConfirmationVisibilityState = remember { mutableStateOf(false) }
    val fileCreationNameConfirmationVisibilityState = remember { mutableStateOf(false) }

    CreateDavResourceDialog(
        title = { Text(text = "Имя новой папки", fontWeight = FontWeight.Bold) },
        visibilityState = folderCreationNameConfirmationVisibilityState,
        onNameValidationFailed = { toast("Введите корректное имя папки") },
        onCreateRequest = onCreateFolder
    )
    CreateDavResourceDialog(
        title = { Text(text = "Имя нового файла", fontWeight = FontWeight.Bold) },
        visibilityState = fileCreationNameConfirmationVisibilityState,
        onNameValidationFailed = { toast("Введите корректное имя файла") },
        onCreateRequest = onCreateFile
    )
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
            PageTitle(title = vaultLocation.lastPathPartOrEmpty().ifEmpty { "/" })
        },
        trailingButton = {
            val menuDisplayedState = remember { mutableStateOf(false) }
            ContextMenu(
                expanded = menuDisplayedState.value,
                items = listOf(
                    object : ContextMenuItem {
                        override val id = "ID_CREATE_DIRECTORY"
                        override val title = "Создать папку здесь"
                        override fun onClick(id: String) {
                            folderCreationNameConfirmationVisibilityState.value = true
                        }
                    },
                    object : ContextMenuItem {
                        override val id = "ID_CREATE_FILE"
                        override val title = "Создать файл"
                        override fun onClick(id: String) {
                            fileCreationNameConfirmationVisibilityState.value = true
                        }
                    }
                ),
                onDismiss = { menuDisplayedState.value = false },
                content = {
                    IconButton(onClick = { menuDisplayedState.value = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Меню"
                        )
                    }
                }
            )
        }
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(
                isRefreshing = directoryListState.value.isLoading
            ),
            onRefresh = onReload
        ) {
            LazyList(
                list = directoryListState.value.data,
                renderItem = { index, davResource ->
                    val contextMenuDisplayedState = remember { mutableStateOf(false) }
                    when (index) {
                        0 -> if (vaultLocation == "/") return@LazyList else IconicListNavItem(
                            title = "..",
                            icon = Icons.Filled.Folder,
                            iconContentDescription = "Папка \"${davResource.path.up()}\"",
                            onPress = {
                                onNavigateToVaultLocation(davResource.path.up())
                            }
                        )
                        else -> ContextMenu(
                            expanded = contextMenuDisplayedState.value,
                            items = listOf(
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