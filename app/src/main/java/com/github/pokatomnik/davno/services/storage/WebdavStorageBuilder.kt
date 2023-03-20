package com.github.pokatomnik.davno.services.storage

import com.github.pokatomnik.davno.services.path.Path
import com.thegrizzlylabs.sardineandroid.DavResource
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.nio.charset.Charset

class WebdavStorageBuilder(private val path: Path) {
    fun build(connectionParams: ConnectionParams): WebdavStorage {
        return WebdavStorage(connectionParams, path)
    }
}