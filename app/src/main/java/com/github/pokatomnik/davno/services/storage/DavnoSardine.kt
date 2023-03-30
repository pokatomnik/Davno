package com.github.pokatomnik.davno.services.storage

import com.thegrizzlylabs.sardineandroid.DavResource
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine
import java.io.InputStream
import java.net.URI

private fun String.trim(strToRemove: String): String {
    return this.removePrefix(strToRemove).removeSuffix(strToRemove)
}

class DavnoDavResource(
    private val davResource: DavResource,
    private val rootUrl: String,
) {
    private fun String.ensureSlash(): String {
        return if (this.startsWith("/")) this else "/$this"
    }

    val extension by lazy {
        if (isDirectory) "" else {
            val parts = name.split('.')
            if (parts.size == 1) "" else {
                parts.last()
            }
        }
    }

    val path: String
        get() {
            val rootPath = URI(rootUrl).path.ensureSlash()
            return davResource.path.removePrefix(rootPath).ensureSlash()
        }

    val absolutePath: String
        get() = davResource.path

    val name: String
        get() = davResource.name

    val isDirectory: Boolean
        get() = davResource.isDirectory
}

class DavnoSardine(
    userName: String,
    password: String,
    private val rootUrl: String,
) {
    private val allowedFileExtensions = setOf("md")

    private val sardine = OkHttpSardine().apply {
        setCredentials(userName, password)
    }

    fun list(relativePath: String): List<DavnoDavResource> {
        val uriStr = joinPaths(rootUrl, relativePath)
        val absolutePath = URI(uriStr).path
        val result = sardine
            .list(uriStr)
            .map {
                DavnoDavResource(it, rootUrl)
            }
            .filter {
                when (it.isDirectory) {
                    true -> {
                        val absolutePathTrimmed = absolutePath.trim("/")
                        val currentPathTrimmed = it.absolutePath.trim("/")
                        absolutePathTrimmed != currentPathTrimmed
                    }
                    false -> this.allowedFileExtensions.contains(it.extension)
                }
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
        return result
    }

    fun getFileContents(relativePath: String): InputStream =
        sardine.get(joinPaths(rootUrl, relativePath))

    fun putFile(relativePath: String, data: ByteArray, mime: String) =
        sardine.put(joinPaths(rootUrl, relativePath), data, mime)

    fun delete(relativePath: String) =
        sardine.delete(joinPaths(rootUrl, relativePath))

    fun createDirectory(relativePath: String) =
        sardine.createDirectory(joinPaths(rootUrl, relativePath))

    fun move(relativePathFrom: String, relativePathTo: String, overwrite: Boolean) =
        sardine.move(
            joinPaths(rootUrl, relativePathFrom),
            joinPaths(rootUrl, relativePathTo),
            overwrite
        )

    fun copy(relativePathFrom: String, relativePathTo: String, overwrite: Boolean) =
        sardine.copy(
            joinPaths(rootUrl, relativePathFrom),
            joinPaths(rootUrl, relativePathTo),
            overwrite
        )
}