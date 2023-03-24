package com.github.pokatomnik.davno

import com.github.pokatomnik.davno.services.storage.fileExt
import com.github.pokatomnik.davno.services.storage.lastPathPartOrEmpty
import org.junit.Test
import org.junit.Assert.assertEquals

class PathUtilsTest {
    @Test
    fun `PathUtils test file ext`() {
        val ext = "file.exe".fileExt()
        assertEquals("exe", ext)
    }

    @Test
    fun `PathUtils test file path ext`() {
        val ext = "/path/to/file.exe".fileExt()
        assertEquals("exe", ext)
    }

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
}