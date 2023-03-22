package com.github.pokatomnik.davno.ui.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.github.pokatomnik.davno.services.storage.WebdavStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class WebdavTesterCallbacks(
    private val webdavStorage: WebdavStorage,
    private val scope: CoroutineScope
) {
    private var _onOk: () -> Unit = {}
    private var _onFail: () -> Unit = {}
    fun onOk(onOk: () -> Unit) {
        _onOk = onOk
    }
    fun onFail(onFail: () -> Unit) {
        _onFail = onFail
    }
    fun test() {
        scope.launch {
            when (webdavStorage.test()) {
                true -> _onOk()
                false -> _onFail()
            }
        }
    }
}

@Composable
fun rememberWebdavConnectionTester(): (
    userName: String,
    password: String,
    rootUrl: String,
) -> WebdavTesterCallbacks {
    val coroutineScope = rememberCoroutineScope()
    return { userName, password, rootUrl ->
        val webdavStorage = WebdavStorage(
            userName = userName,
            password = password,
            rootPath = rootUrl
        )
        WebdavTesterCallbacks(
            webdavStorage = webdavStorage,
            scope = coroutineScope
        )
    }
}