package com.github.pokatomnik.davno.services.storage

private fun removeSlashes(input: String): String {
    return input.removePrefix("/").removeSuffix("/");
}

private fun joinTwoPaths(path1: String, path2: String): String {
    val path1Cleaned = removeSlashes(path1)
    val path2Cleaned = removeSlashes(path2)
    return "$path1Cleaned/$path2Cleaned"
}

fun joinPaths(vararg paths: String): String {
    return if (paths.isEmpty()) "" else paths.reduce { acc, current ->
        joinTwoPaths(acc, current)
    }
}

fun String.up(): String {
    val startsWithSlash = this.startsWith("/")
    val parent = this
        .split("/")
        .filter { it.isNotEmpty() }
        .dropLast(1)
        .joinToString("/")
    return if (startsWithSlash) "/$parent" else parent
}

fun String.fileExt(): String {
    val parts = this.split('.')
    return if (parts.size == 1) "" else {
        parts.last()
    }
}

fun String.lastPathPartOrEmpty(): String {
    val parts = this.split('/').filter { it.isNotEmpty() }
    if (parts.isEmpty()) return ""
    return parts.last()
}