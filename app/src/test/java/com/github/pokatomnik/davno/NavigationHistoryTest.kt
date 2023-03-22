package com.github.pokatomnik.davno

import com.github.pokatomnik.davno.screens.vaultview.NavigationHistory
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationHistoryTest {
    private fun <T : Any> NavigationHistory<T>.serializeHistory(serializer: (T) -> String): String {
        while (canGoBackward) {
            moveBackward()
        }
        val values = mutableListOf<String>()
        while (canGoForward) {
            values.add(serializer(currentValue))
            moveForward()
        }
        values.add(serializer(currentValue))
        return values.joinToString(",")
    }

    @Test
    fun `NavigationHistory pushes test`() {
        val navigationHistory = NavigationHistory("a").apply {
            push("b")
            push("c")
            push("d")
        }
        assertEquals(
            "a,b,c,d",
            navigationHistory.serializeHistory { it },
        )
    }

    @Test
    fun `NavigationHistory only one element`() {
        val navigationHistory = NavigationHistory("a")
        assertEquals("a", navigationHistory.serializeHistory { it })
    }

    @Test
    fun `NavigationHistory flushes when navigated back`() {
        val navigationHistory = NavigationHistory("a").apply {
            push("b")
            push("c")
            push("d")
        }
        navigationHistory.apply {
            moveBackward()
            moveBackward()
        }
        navigationHistory.apply {
            push("e")
            push("f")
        }
        assertEquals("a,b,e,f", navigationHistory.serializeHistory { it })
    }

    @Test
    fun `NavigationHistory flushes when navigated back and forward`() {
        val navigationHistory = NavigationHistory("a").apply {
            push("b")
            push("c")
            push("d")
        }
        navigationHistory.apply {
            moveBackward()
            moveBackward()
        }
        navigationHistory.apply {
            moveForward()
            moveForward()
        }
        navigationHistory.apply {
            push("e")
            push("f")
        }
        assertEquals("a,b,c,d,e,f", navigationHistory.serializeHistory { it })
    }

    @Test
    fun `NavigationHistory test canGoBackward`() {
        val navigationHistory = NavigationHistory("a").apply {
            push("b")
        }
        assertEquals(true, navigationHistory.canGoBackward)
        navigationHistory.moveBackward()
        assertEquals(false, navigationHistory.canGoBackward)
    }

    @Test
    fun `NavigationHistory test canGoForward`() {
        val navigationHistory = NavigationHistory("a").apply {
            push("b")
        }
        assertEquals(false, navigationHistory.canGoForward)
        navigationHistory.moveBackward()
        assertEquals(true, navigationHistory.canGoForward)
    }

    @Test
    fun `NavigationHistory test moveBackward return result`() {
        val navigationHistory = NavigationHistory("a").apply {
            push("b")
        }
        assertEquals(true, navigationHistory.moveBackward())
        assertEquals(false, navigationHistory.moveBackward())
    }

    @Test
    fun `NavigationHistory test moveForward return result`() {
        val navigationHistory = NavigationHistory("a").apply {
            push("b")
        }
        assertEquals(false, navigationHistory.moveForward())
        navigationHistory.moveBackward()
        assertEquals(true, navigationHistory.moveForward())
    }

    @Test
    fun `NavigationHistory test index`() {
        val navigationHistory = NavigationHistory("a")
        assertEquals(0, navigationHistory.index)

        navigationHistory.push("b")
        assertEquals(1, navigationHistory.index)

        navigationHistory.push("c")
        assertEquals(2, navigationHistory.index)

        navigationHistory.moveBackward()
        assertEquals(1, navigationHistory.index)

        navigationHistory.moveForward()
        assertEquals(2, navigationHistory.index)

        navigationHistory.apply {
            moveBackward()
            push("d")
        }
        assertEquals(2, navigationHistory.index)
    }
}