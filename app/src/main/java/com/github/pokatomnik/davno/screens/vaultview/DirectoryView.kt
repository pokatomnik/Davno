package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.github.pokatomnik.davno.services.storage.lastPathPartOrEmpty
import com.github.pokatomnik.davno.services.storage.up
import com.github.pokatomnik.davno.ui.components.IconicListNavItem
import com.github.pokatomnik.davno.ui.components.LazyList
import com.github.pokatomnik.davno.ui.components.PageContainer
import com.github.pokatomnik.davno.ui.components.PageTitle
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
) {
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
            if (history.canGoForward) {
                IconButton(onClick = { history.moveForward() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Вперед"
                    )
                }
            }
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