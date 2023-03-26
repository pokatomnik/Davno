package com.github.pokatomnik.davno.screens.vaultview.file

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.github.pokatomnik.davno.ui.components.PageContainer
import com.github.pokatomnik.davno.ui.components.PageTitle
import com.github.pokatomnik.davno.ui.components.makeToast

@Composable
fun FileEdit(
    fileName: String,
    isLoading: Boolean,
    contentMarkdownInitial: String,
    onSave: (content: String) -> Unit,
    onExit: () -> Unit,
) {
    val toast = makeToast()
    val markdownState = remember(contentMarkdownInitial) {
        mutableStateOf(contentMarkdownInitial)
    }
    val focusRequester = remember { FocusRequester() }

    val handleSave: () -> Unit = {
        if (isLoading) {
            toast("Подождите, еще загружается")
        } else {
            onSave(markdownState.value)
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
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
            PageTitle(title = fileName)
        },
        trailingButton = {
            IconButton(onClick = handleSave) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = "Включить режим редактирования"
                )
            }
        }
    ) {
        TextField(
            value = markdownState.value,
            onValueChange = { markdownState.value = it },
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester),
        )
    }
}