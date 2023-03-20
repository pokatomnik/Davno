package com.github.pokatomnik.davno.services.storage

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun rememberWebdavStorage(): WebdavStorage {
    return hiltViewModel<WebdavStorageViewModel>().webdavStorage
}