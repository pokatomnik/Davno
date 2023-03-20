package com.github.pokatomnik.davno.services.serializer

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun rememberSerializer(): Serializer {
    return hiltViewModel<SerializerViewModel>().serializer
}