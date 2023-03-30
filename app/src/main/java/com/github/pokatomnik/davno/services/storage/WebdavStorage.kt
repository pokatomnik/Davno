package com.github.pokatomnik.davno.services.storage

import com.github.pokatomnik.davno.services.logger.DavnoLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.nio.charset.Charset

class WebdavStorage(
    userName: String,
    password: String,
    rootPath: String,
    private val logger: DavnoLogger
) {
    private val context = Dispatchers.IO + SupervisorJob()

    private val sardine = DavnoSardine(
        userName = userName,
        password = password,
        rootUrl = rootPath
    )

    suspend fun list(path: String) = withContext(context) {
        try {
            sardine.list(path)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown list error",
                extra = e.stackTraceToString()
            )
            throw e
        }
    }

    suspend fun test(): Boolean {
        return try {
            list(path = "/")
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getFileContents(path: String): String = withContext(context) {
        val inputStream = try {
            sardine.getFileContents(path)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown getting file contents error",
                extra = e.stackTraceToString()
            )
            throw e
        }
        val inputStreamReader = InputStreamReader(inputStream)
        inputStreamReader.readText()
    }

    suspend fun putFile(path: String, data: String, mime: String = "text/markdown") = withContext(context) {
        val bytes = data.toByteArray(Charset.defaultCharset())
        try {
            sardine.putFile(path, bytes, mime)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown file creation error",
                extra = e.stackTraceToString()
            )
            throw e
        }
    }

    suspend fun delete(path: String) = withContext(context) {
        try {
            sardine.delete(path)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown deleting file error",
                extra = e.stackTraceToString()
            )
            throw e
        }
    }

    suspend fun createDirectory(path: String) = withContext(context) {
        try {
            sardine.createDirectory(path)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown directory creation error",
                extra = e.stackTraceToString()
            )
            throw e
        }
    }

    suspend fun move(fromPath: String, toPath: String) = withContext(context) {
        try {
            sardine.move(fromPath, toPath, true)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown file moving error",
                extra = e.stackTraceToString()
            )
            throw e
        }
    }

    // TODO add rename

    /**
     * Copies a file. Overwrites if a destination file exists.
     */
    suspend fun copy(fromPath: String, toPath: String) = withContext(context) {
        try {
            sardine.copy(fromPath, toPath, true)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown file copying error",
                extra = e.stackTraceToString()
            )
            throw e
        }
    }
}
