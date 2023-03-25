package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.github.pokatomnik.davno.services.storage.lastPathPartOrEmpty
import com.github.pokatomnik.davno.services.storage.up
import com.github.pokatomnik.davno.ui.components.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.thegrizzlylabs.sardineandroid.DavResource

@Composable
fun DirectoryView(
    history: History<String>,
    directoryListState: MutableState<DavResourceState<List<DavResource>>>,
    onNavigateBack: () -> Unit,
    onReload: () -> Unit,
    onFilePress: (file: DavResource) -> Unit,
    onCreateFolder: (name: String) -> Unit,
) {
    val alertDisplayedState = remember { mutableStateOf(false) }

    CreateFolderDialog(
        visibilityState = alertDisplayedState,
        onCreateFolder = onCreateFolder
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
            PageTitle(title = history.currentValue.lastPathPartOrEmpty().ifEmpty { "/" })
        },
        trailingButton = {
            val menuDisplayedState = remember { mutableStateOf(false) }
            ContextMenu(
                expanded = menuDisplayedState.value,
                items = (if (history.canGoForward) listOf(
                    object : ContextMenuItem {
                        override val id = "ID_GO_FORWARD"
                        override val title = "Вперед"
                        override fun onClick(id: String) { history.moveForward() }
                    }
                ) else listOf()) +
                    listOf(
                        object : ContextMenuItem {
                            override val id = "ID_CREATE_DIRECTORY"
                            override val title = "Создать папку здесь"
                            override fun onClick(id: String) { alertDisplayedState.value = true }
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
                    when (index) {
                        0 -> if (history.currentValue == "/") return@LazyList else IconicListNavItem(
                            title = "..",
                            icon = Icons.Filled.Folder,
                            iconContentDescription = "Папка \"${davResource.path.up()}\"",
                            onPress = {
                                history.push(davResource.path.up())
                            }
                        )
                        else -> IconicListNavItem(
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
                                    history.push(davResource.path)
                                } else {
                                    onFilePress(davResource)
                                }
                            }
                        )
                    }
                }
            )
        }
    }
}