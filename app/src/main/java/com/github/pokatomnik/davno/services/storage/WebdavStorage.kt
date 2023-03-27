package com.github.pokatomnik.davno.services.storage

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
        val webdavResources = sardine.list(absolutePath)
        webDavResourceFilters.fold(webdavResources) {
            currentFilteredList,
            davFilter ->
            davFilter.filterWebdavResourceList(currentFilteredList)
        }
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
        val inputStream = sardine.get(absolutePath)
        val inputStreamReader = InputStreamReader(inputStream)
        inputStreamReader.readText()
    }

    suspend fun putFile(relativeFilePath: String, data: String, mime: String = "text/markdown") = withContext(context) {
        val absolutePath = joinPaths(rootPath, relativeFilePath)
        sardine.put(
            absolutePath,
            data.toByteArray(Charset.defaultCharset()),
            mime
        )
    }

    suspend fun delete(relativePath: String) = withContext(context) {
        val absolutePath = joinPaths(rootPath, relativePath)
        sardine.delete(absolutePath)
    }

    suspend fun createDirectory(relativePath: String) = withContext(context) {
        val absolutePath = joinPaths(rootPath, relativePath)
        sardine.createDirectory(absolutePath)
    }

    /**
     * Moves a file. Overwrites if a destination file exists.
     */
    suspend fun moveFile(relativePathFrom: String, relativePathTo: String) = withContext(context) {
        val absolutePathFrom = joinPaths(rootPath, relativePathFrom)
        val absolutePathTo = joinPaths(rootPath, relativePathTo)
        sardine.move(absolutePathFrom, absolutePathTo, true)
    }

    // TODO add rename

    /**
     * Copies a file. Overwrites if a destination file exists.
     */
    suspend fun copyFile(relativePathFrom: String, relativePathTo: String) = withContext(context) {
        val absolutePathFrom = joinPaths(rootPath, relativePathFrom)
        val absolutePathTo = joinPaths(rootPath, relativePathTo)
        sardine.copy(absolutePathFrom, absolutePathTo, true)
    }

    suspend fun existsFile(relativePath: String) = withContext(context) {
        val absolutePath = joinPaths(rootPath, relativePath)
        sardine.exists(absolutePath)
    }
}