package com.github.pokatomnik.davno.services.storage

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun rememberWebdavStorageBuilder(): WebdavStorageBuilder {
    return hiltViewModel<WebdavStorageViewModel>().webdavStorageBuilder
}