package com.github.pokatomnik.davno.services.storage

import com.github.pokatomnik.davno.services.logger.DavnoLogger
import com.github.pokatomnik.davno.services.storage.filters.Markdown
import com.github.pokatomnik.davno.services.storage.filters.WebdavResourceListFilter
import com.thegrizzlylabs.sardineandroid.DavResource
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.nio.charset.Charset

class WebdavStorage(
    private val userName: String,
    private val password: String,
    private val rootPath: String,
    private val logger: DavnoLogger
) {
    private val context = Dispatchers.IO + SupervisorJob()

    private val sardine = OkHttpSardine().apply {
        setCredentials(userName, password)
    }

    private val webDavResourceFilters = listOf<WebdavResourceListFilter>(
        Markdown()
    )

    suspend fun list(relativePath: String): List<DavResource> = withContext(context) {
        val absolutePath = joinPaths(rootPath, relativePath)
        val webdavResources = try {
            sardine.list(absolutePath)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown list error",
                extra = e.stackTraceToString()
            )
            throw e
        }
        normalizeWebdavList(webdavResources, webDavResourceFilters)
    }

    suspend fun test(): Boolean {
        return try {
            list("/")
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getFileContents(relativeFilePath: String): String = withContext(context) {
        val absolutePath = joinPaths(rootPath, relativeFilePath)
        val inputStream = try {
            sardine.get(absolutePath)
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

    suspend fun putFile(relativeFilePath: String, data: String, mime: String = "text/markdown") = withContext(context) {
        val absolutePath = joinPaths(rootPath, relativeFilePath)
        try {
            sardine.put(
                absolutePath,
                data.toByteArray(Charset.defaultCharset()),
                mime
            )
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown file creation error",
                extra = e.stackTraceToString()
            )
            throw e
        }
    }

    suspend fun delete(relativePath: String) = withContext(context) {
        val absolutePath = joinPaths(rootPath, relativePath)
        try {
            sardine.delete(absolutePath)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown deleting file error",
                extra = e.stackTraceToString()
            )
            throw e
        }
    }

    suspend fun createDirectory(relativePath: String) = withContext(context) {
        val absolutePath = joinPaths(rootPath, relativePath)
        try {
            sardine.createDirectory(absolutePath)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown directory creation error",
                extra = e.stackTraceToString()
            )
            throw e
        }
    }

    suspend fun move(relativePathFrom: String, relativePathTo: String) = withContext(context) {
        val absolutePathFrom = joinPaths(rootPath, relativePathFrom)
        val absolutePathTo = joinPaths(rootPath, relativePathTo)
        try {
            sardine.move(absolutePathFrom, absolutePathTo, true)
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
    suspend fun copy(relativePathFrom: String, relativePathTo: String) = withContext(context) {
        val absolutePathFrom = joinPaths(rootPath, relativePathFrom)
        val absolutePathTo = joinPaths(rootPath, relativePathTo)
        try {
            sardine.copy(absolutePathFrom, absolutePathTo, true)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown file copying error",
                extra = e.stackTraceToString()
            )
            throw e
        }

    }

    suspend fun existsFile(relativePath: String) = withContext(context) {
        val absolutePath = joinPaths(rootPath, relativePath)
        try {
            sardine.exists(absolutePath)
        } catch (e: Exception) {
            logger.error(
                message = e.message ?: "Unknown file exists checking error",
                extra = e.stackTraceToString()
            )
            throw e
        }
    }
}

/**
 * Places directories at the start of the list
 * and files at the end of the list,
 * sorts both.
 */
fun normalizeWebdavList(
    webdavResponse: List<DavResource>,
    webDavResourceFilters: List<WebdavResourceListFilter>
) = webDavResourceFilters
    .fold(webdavResponse) {
            currentFilteredList,
            davFilter ->
        davFilter.filterWebdavResourceList(currentFilteredList)
    }
    .sortedWith { a, b ->
        val bothSameType = (a.isDirectory && b.isDirectory) ||
                (!a.isDirectory && !b.isDirectory)
        if (bothSameType) {
            a.name.compareTo(b.name)
        } else if (a.isDirectory && !b.isDirectory) {
            -1
        } else {
            1
        }
    }