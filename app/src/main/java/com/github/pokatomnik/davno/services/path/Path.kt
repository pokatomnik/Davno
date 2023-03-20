package com.github.pokatomnik.davno.services.path

class Path {
    private fun removeSlashes(input: String): String {
        return input.removePrefix("/").removeSuffix("/");
    }

    private fun joinTwoPaths(path1: String, path2: String): String {
        val path1Cleaned = removeSlashes(path1)
        val path2Cleaned = removeSlashes(path2)
        return "$path1Cleaned/$path2Cleaned"
    }

    fun join(vararg paths: String): String {
        return if (paths.isEmpty()) "" else paths.reduce { acc, current ->
            joinTwoPaths(acc, current)
        }
    }
}