package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import com.github.pokatomnik.davno.screens.vaultview.storage.up
import com.github.pokatomnik.davno.ui.components.LazyList
import com.github.pokatomnik.davno.ui.components.ListNavItem
import com.github.pokatomnik.davno.ui.components.PageContainer
import com.github.pokatomnik.davno.ui.components.PageTitle
import com.thegrizzlylabs.sardineandroid.DavResource

@Composable
fun WebdavNavigator(
    history: History<String>,
    webdavStorage: WebdavStorage,
    onNavigateBackToVaultSelector: () -> Unit,
) {
    val directoryListState = remember(history.currentValue, webdavStorage) {
        mutableStateOf<List<DavResource>>(listOf())
    }.apply {
        LaunchedEffect(webdavStorage, history.currentValue) {
            value = webdavStorage.list(history.currentValue)
        }
    }

    val navigateBack: () -> Unit = {
        if (history.canGoBackward) {
            history.moveBackward()
        } else {
            onNavigateBackToVaultSelector()
        }
    }

    PageContainer(
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
        }
    ) {
        LazyList(
            list = directoryListState.value,
            renderItem = { index, davResource ->
                when (index) {
                    0 -> if (history.currentValue == "/") return@LazyList else ListNavItem(
                        title = "..",
                        onPress = {
                            history.push(davResource.path.up())
                        }
                    )
                    else -> ListNavItem(
                        title = davResource.name,
                        onPress = {
                            if (davResource.isDirectory) {
                                history.push(davResource.path)
                            } else {
                                // TODO implement file view
                            }
                        }
                    )
                }
            }
        )
    }
}
