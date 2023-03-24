package com.github.pokatomnik.davno

import com.github.pokatomnik.davno.services.storage.fileExt
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
}