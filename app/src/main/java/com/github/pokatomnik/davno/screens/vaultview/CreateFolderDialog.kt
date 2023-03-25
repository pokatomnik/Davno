package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.pokatomnik.davno.services.storage.allowedFilesystemName
import com.github.pokatomnik.davno.ui.components.LARGE_PADDING
import com.github.pokatomnik.davno.ui.components.SMALL_PADDING
import com.github.pokatomnik.davno.ui.components.makeToast

@Composable
fun CreateFolderDialog(
    visibilityState: MutableState<Boolean>,
    onCreateFolder: (name: String) -> Unit,
) {
    val toast = makeToast()
    val textState = remember { mutableStateOf("") }
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
                Text(text = "Имя новой папки", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(SMALL_PADDING.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = {
                            visibilityState.value = false
                            textState.value = ""
                        }
                    ) {
                        Text(text = "Отмена")
                    }
                    Spacer(modifier = Modifier.width(SMALL_PADDING.dp))
                    TextButton(
                        onClick = {
                            val name = textState.value
                            if (name.allowedFilesystemName()) {
                                visibilityState.value = false
                                textState.value = ""
                                onCreateFolder(name)
                            } else {
                                toast("Введите корректное имя папки")
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