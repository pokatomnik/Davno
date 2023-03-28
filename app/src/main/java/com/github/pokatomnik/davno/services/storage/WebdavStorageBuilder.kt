package com.github.pokatomnik.davno.services.storage

import com.github.pokatomnik.davno.services.logger.DavnoLogger

class WebdavStorageBuilder(private val logger: DavnoLogger) {
    fun build(
        userName: String,
        password: String,
        rootPath: String,
    ) = WebdavStorage(userName, password, rootPath, logger)
}