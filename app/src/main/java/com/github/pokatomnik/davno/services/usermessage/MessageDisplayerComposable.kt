package com.github.pokatomnik.davno.services.usermessage

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun rememberMessageDisplayer(): MessageDisplayer {
    return hiltViewModel<MessageDisplayerViewModel>().messageDisplayer
}