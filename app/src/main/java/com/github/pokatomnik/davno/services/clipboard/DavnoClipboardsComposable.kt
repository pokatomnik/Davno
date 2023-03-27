package com.github.pokatomnik.davno.services.clipboard

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun rememberClipboards(): Clipboards {
    return hiltViewModel<DavnoClipboardsViewModel>().clipboards
}