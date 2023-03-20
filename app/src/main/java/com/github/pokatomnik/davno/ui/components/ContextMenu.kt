package com.github.pokatomnik.davno.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

interface ContextMenuItem {
    val id: String
    val title: String
    fun onClick(id: String)
}

@Composable
fun <T : ContextMenuItem> ContextMenu(
    expanded: Boolean,
    items: List<T>,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (expanded) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        ) {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        items.forEach { menuItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(SMALL_LIST_ITEM_HEIGHT.dp)
                                    .clickable(
                                        indication = rememberRipple(bounded = true),
                                        interactionSource = remember { MutableInteractionSource() },
                                        onClick = {
                                            onDismiss()
                                            menuItem.onClick(menuItem.id)
                                        }
                                    )
                                    .padding(
                                        horizontal = LARGE_PADDING.dp
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = menuItem.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            )
        }
    }
    content()
}