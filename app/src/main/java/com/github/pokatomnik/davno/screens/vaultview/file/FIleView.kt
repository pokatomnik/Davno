package com.github.pokatomnik.davno.screens.vaultview.file

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.pokatomnik.davno.ui.components.PageContainer
import com.github.pokatomnik.davno.ui.components.PageTitle
import com.github.pokatomnik.davno.ui.components.SMALL_PADDING
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun FileView(
    fileName: String,
    isLoading: Boolean,
    contentMarkdown: String,
    reload: () -> Unit,
    onEnableEdit: () -> Unit,
    onExit: () -> Unit,
) {
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
            PageTitle(title = fileName)
        },
        trailingButton = {
            IconButton(onClick = onEnableEdit) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Включить режим редактирования"
                )
            }
        }
    ) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(
                isRefreshing = isLoading
            ),
            onRefresh = reload,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = SMALL_PADDING.dp)
            ) {
                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
                MarkdownText(
                    markdown = contentMarkdown,
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Justify,
                )
                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
            }
        }
    }
}