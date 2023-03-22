package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import com.github.pokatomnik.davno.screens.vaultview.storage.up
import com.github.pokatomnik.davno.ui.components.*
import com.github.pokatomnik.davno.ui.components.PageTitle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
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

    val navigateBack: () -> Unit = {
        if (history.canGoBackward) {
            history.moveBackward()
        } else {
            onNavigateBackToVaultSelector()
        }
    }

    when (val fileViewPath = fileViewPathState.value) {
        null -> PageContainer(
            priorButton = {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            },
            header = {
                PageTitle(title = "Просмотр")
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
                onRefresh = reloadDavResources
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
                                        fileViewPathState.value = davResource.path
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
        else -> FileView(
            path = fileViewPath,
            webdavStorage = webdavStorage,
            onExit = { fileViewPathState.value = null }
        )
    }

}
