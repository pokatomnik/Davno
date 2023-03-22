package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import com.github.pokatomnik.davno.ui.components.PageContainer
import com.github.pokatomnik.davno.ui.components.PageTitle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch

@Composable
fun FileView(
    path: String,
    webdavStorage: WebdavStorage,
    onExit: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val contentState = remember {
        mutableStateOf(
            DavResourceState(
                isLoading = false,
                errorText = null,
                data = ""
            )
        )
    }

    val reloadDavResourceText: () -> Unit = {
        coroutineScope.launch {
            contentState.value = DavResourceState(
                isLoading = true,
                errorText = null,
                data = contentState.value.data
            )
            contentState.value = try {
                val content = webdavStorage.getFileContents(path)
                DavResourceState(
                    isLoading = false,
                    errorText = null,
                    data = content
                )
            } catch (e: Exception) {
                DavResourceState(
                    isLoading = false,
                    errorText = e.message ?: "Неизвестная ошибка загрузки",
                    data = ""
                )
            }
        }
    }

    LaunchedEffect(path, webdavStorage) {
        reloadDavResourceText()
    }

    PageContainer(
        priorButton = {
            IconButton(onClick = onExit) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Закрыть"
                )
            }
        },
        header = {
            PageTitle(title = "Файл")
        }
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(
                isRefreshing = contentState.value.isLoading
            ),
            onRefresh = reloadDavResourceText
        ) {
           Column(
               modifier = Modifier
                   .fillMaxHeight()
                   .verticalScroll(rememberScrollState())
           ) {
               MarkdownText(
                   markdown = contentState.value.data,
                   modifier = Modifier.fillMaxSize(),
                   textAlign = TextAlign.Justify,
               )
           }
        }
    }
}