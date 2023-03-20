package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import com.github.pokatomnik.davno.ui.components.LazyList
import com.github.pokatomnik.davno.ui.components.ListNavItem
import com.github.pokatomnik.davno.ui.components.PageContainer
import com.github.pokatomnik.davno.ui.components.PageTitle
import com.thegrizzlylabs.sardineandroid.DavResource

@Composable
fun DirectoryLister(
    pathState: MutableState<String>,
    webdavStorage: WebdavStorage,
    onNavigateBack: () -> Unit,
) {
    val directoryListState = remember(pathState.value, webdavStorage) {
        mutableStateOf<List<DavResource>>(listOf())
    }
    LaunchedEffect(webdavStorage, pathState.value) {
        directoryListState.value = webdavStorage.list(pathState.value)
    }
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
            PageTitle(title = "Просмотр")
        }
    ) {
        LazyList(
            list = directoryListState.value,
            renderItem = { _, davResource ->
                ListNavItem(
                    title = davResource.name,
                    onPress = {
                        pathState.value = davResource.path
                    }
                )
            }
        )
    }
}