package com.github.pokatomnik.davno.services.storage

private fun removeSlashes(input: String): String {
    return input.removePrefix("/").removeSuffix("/")
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

/**
 * Ensures the string as a file has an extention.
 * Provided argument should be an extension string
 * without dot.
 *
 * Example: `md`, 'txt', etc.
 */
fun String.ensureHasExtension(extension: String): String {
    return if (this.endsWith(".$extension")) {
        this
    } else {
        "$this.$extension"
    }
}

fun String.lastPathPartOrEmpty(): String {
    val parts = this.split('/').filter { it.isNotEmpty() }
    if (parts.isEmpty()) return ""
    return parts.last()
}

private val allowedAlphabet = "abcdefghijklmnopqrstuvwxyzабвгдеёжзийклмнопрстуфхцчшщъыьэюя"
    .let { it + it.uppercase() }
    .plus(" _-()[].")
    .plus("0123456789".split("").toSet())
fun String.allowedFilesystemName(): Boolean {
    for (char in this@allowedFilesystemName) {
        if (!allowedAlphabet.contains(char)) {
            return false
        }
    }
    return true
}