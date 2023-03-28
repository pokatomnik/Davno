package com.github.pokatomnik.davno.screens.vaultview.directory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.pokatomnik.davno.services.storage.allowedFilesystemName
import com.github.pokatomnik.davno.ui.components.LARGE_PADDING
import com.github.pokatomnik.davno.ui.components.SMALL_PADDING

@Composable
fun CreateDavResourceDialog(
    title: @Composable () -> Unit,
    visibilityState: MutableState<Boolean>,
    onNameValidationFailed: () -> Unit,
    onCreateRequest: (name: String) -> Unit,
) {
    val nameState = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    if (!visibilityState.value) return

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = { visibilityState.value = false },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(SMALL_PADDING.dp))
                    .background(MaterialTheme.colors.background)
                    .padding(SMALL_PADDING.dp)
            ) {
                Spacer(modifier = Modifier.height(LARGE_PADDING.dp))
                title()
                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
                TextField(
                    value = nameState.value,
                    onValueChange = { nameState.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    maxLines = 1,
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            visibilityState.value = false
                            nameState.value = ""
                        }
                    ) {
                        Text(text = "Отмена")
                    }
                    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                    TextButton(
                        onClick = {
                            val name = nameState.value
                            if (name.allowedFilesystemName()) {
                                visibilityState.value = false
                                nameState.value = ""
                                onCreateRequest(name)
                            } else {
                                onNameValidationFailed()
                            }
                        }
                    ) {
                        Text(text = "Создать")
                    }
                }
            }
        }
    )
}