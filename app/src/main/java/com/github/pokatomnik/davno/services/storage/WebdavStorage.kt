package com.github.pokatomnik.davno.services.storage

import com.github.pokatomnik.davno.services.path.Path
import com.thegrizzlylabs.sardineandroid.DavResource
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import java.nio.charset.Charset

class WebdavStorage(private val connectionParams: ConnectionParams, private val path: Path) {
    private val context = Dispatchers.IO + SupervisorJob()

    private val sardine = OkHttpSardine().apply {
        setCredentials(connectionParams.userName, connectionParams.password)
    }

    suspend fun list(relativePath: String): List<DavResource> = withContext(context) {
        val absolutePath = path.join(connectionParams.rootPath, relativePath)
        sardine.list(absolutePath)
    }

    suspend fun getFileContents(relativeFilePath: String): String = withContext(context) {
        val absolutePath = path.join(connectionParams.rootPath, relativeFilePath)
        val inputStream = sardine.get(absolutePath)
        val inputStreamReader = InputStreamReader(inputStream)
        inputStreamReader.readText()
    }

    suspend fun putFile(relativeFilePath: String, data: String, mime: String = "text/markdown") = withContext(context) {
        val absolutePath = path.join(connectionParams.rootPath, relativeFilePath)
        sardine.put(
            absolutePath,
            data.toByteArray(Charset.defaultCharset()),
            mime
        )
    }

    suspend fun delete(relativePath: String) = withContext(context) {
        val absolutePath = path.join(connectionParams.rootPath, relativePath)
        sardine.delete(absolutePath)
    }

    suspend fun createDirectory(relativePath: String) = withContext(context) {
        val absolutePath = path.join(connectionParams.rootPath, relativePath)
        sardine.createDirectory(absolutePath)
    }

    /**
     * Moves a file. Overwrites if a destination file exists.
     */
    suspend fun moveFile(relativePathFrom: String, relativePathTo: String) = withContext(context) {
        val absolutePathFrom = path.join(connectionParams.rootPath, relativePathFrom)
        val absolutePathTo = path.join(connectionParams.rootPath, relativePathTo)
        sardine.move(absolutePathFrom, absolutePathTo, true)
    }

    /**
     * Copies a file. Overwrites if a destination file exists.
     */
    suspend fun copyFile(relativePathFrom: String, relativePathTo: String) = withContext(context) {
        val absolutePathFrom = path.join(connectionParams.rootPath, relativePathFrom)
        val absolutePathTo = path.join(connectionParams.rootPath, relativePathTo)
        sardine.copy(absolutePathFrom, absolutePathTo, true)
    }

    suspend fun existsFile(relativePath: String) = withContext(context) {
        val absolutePath = path.join(connectionParams.rootPath, relativePath)
        sardine.exists(absolutePath)
    }
}