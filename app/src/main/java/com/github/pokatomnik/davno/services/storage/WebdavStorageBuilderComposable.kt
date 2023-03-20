package com.github.pokatomnik.davno.services.storage

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun rememberWebdavStorage(): WebdavStorageBuilder {
    return hiltViewModel<WebdavStorageBuilderViewModel>().webdavStorageBuilder
}