package com.github.pokatomnik.davno.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

interface ConfirmationModalButton {
    val id: String
    val text: String
    fun onClick(id: String)
}

@Composable
fun <T : ConfirmationModalButton>ConfirmationModal(
    visibilityState: MutableState<Boolean>,
    buttonOk: T,
    buttonCancel: T,
    confirmationText: @Composable () -> Unit,
    title: @Composable () -> Unit,
) {
    if (!visibilityState.value) return
    AlertDialog(
        onDismissRequest = {
            visibilityState.value = false
        },
        title = title,
        text = confirmationText,
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = LARGE_PADDING.dp,
                        end = LARGE_PADDING.dp,
                        bottom = SMALL_PADDING.dp
                    ),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        visibilityState.value = false
                        buttonCancel.onClick(buttonCancel.id)
                    },
                    content = { Text(text = buttonCancel.text) }
                )
                Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                TextButton(
                    onClick = {
                        visibilityState.value = false
                        buttonOk.onClick(buttonOk.id)
                    },
                    content = { Text(text = buttonOk.text) }
                )
            }
        }
    )
}