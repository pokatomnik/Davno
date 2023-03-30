package com.github.pokatomnik.davno

import com.github.pokatomnik.davno.services.storage.ensureHasExtension
import com.github.pokatomnik.davno.services.storage.lastPathPartOrEmpty
import org.junit.Test
import org.junit.Assert.assertEquals

class PathUtilsTest {
    @Test
    fun `PathUtils test last part`() {
        val lastPart = "/path/to/folder".lastPathPartOrEmpty()
        assertEquals("folder", lastPart)
    }

    @Test
    fun `PathUtils test last part only one part`() {
        val lastPart = "/path/".lastPathPartOrEmpty()
        assertEquals(lastPart, "path")
    }

    @Test
    fun `PathUtils test last part only one slash`() {
        val lastPart = "/".lastPathPartOrEmpty()
        assertEquals("", lastPart)
    }

    @Test
    fun `PathUtils test last part empty`() {
        val lastPart = "".lastPathPartOrEmpty()
        assertEquals("", lastPart)
    }

    @Test
    fun `PathUtils test if file name with extension has an extension`() {
        val name = "/a/b/c/file.md".ensureHasExtension("md")
        assertEquals("/a/b/c/file.md", name)
    }

    @Test
    fun `PathUtils test if file name without extension has an extension`() {
        val name = "/a/b/c/file".ensureHasExtension("md")
        assertEquals("/a/b/c/file.md", name)
    }
}