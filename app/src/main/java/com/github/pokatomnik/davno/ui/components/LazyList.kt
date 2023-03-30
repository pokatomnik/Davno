package com.github.pokatomnik.davno.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T : Any?> LazyList(
    modifier: Modifier = Modifier,
    list: List<T>,
    lazyListState: LazyListState = rememberLazyListState(),
    renderItem: @Composable (index: Int, item: T) -> Unit
) {
    LazyColumn(state = lazyListState, modifier = modifier.fillMaxSize()) {
        itemsIndexed(list) { index, item ->
            renderItem(index, item)
        }
    }
}