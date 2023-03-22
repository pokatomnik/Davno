package com.github.pokatomnik.davno.screens.vaultview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class NavigationEntry<T : Any>(
    val value: T,
    var prev: NavigationEntry<T>?,
    var next: NavigationEntry<T>?
)

interface History<T : Any> {
    val currentValue: T
    val index: Int
    val canGoBackward: Boolean
    val canGoForward: Boolean
    fun push(value: T)
    fun moveBackward(): Boolean
    fun moveForward(): Boolean
}

class NavigationHistory<T : Any>(startItem: T) : History<T> {
    private var _index = 0

    private var currentEntry = NavigationEntry(
        value = startItem,
        prev = null,
        next = null,
    )

    override val index: Int
        get() = _index

    override val currentValue: T
        get() = currentEntry.value

    override val canGoBackward: Boolean
        get() = currentEntry.prev != null

    override val canGoForward: Boolean
        get() = currentEntry.next != null

    /**
     * Pushes a new value to history and flushes
     * all next entries (if they are)
     */
    override fun push(value: T) {
        val nextEntry = NavigationEntry(
            value = value,
            prev = currentEntry,
            next = null
        )
        currentEntry.next = nextEntry
        currentEntry = nextEntry
        _index++
    }

    /**
     * Sets the current entry as a previous one
     * @return `true` if navigated or `false` otherwise
     */
    override fun moveBackward(): Boolean {
        var navigated = false
        currentEntry.prev?.let { prevEntry ->
            currentEntry = prevEntry
            navigated = true
            _index--
        }
        return navigated
    }

    /**
     * Sets the current entry as the next one
     * @return `true` if navigated or `false` otherwise
     */
    override fun moveForward(): Boolean {
        var navigated = false
        currentEntry.next?.let { nextEntry ->
            currentEntry = nextEntry
            navigated = true
            _index++
        }
        return navigated
    }
}

@Composable
fun <T : Any>rememberHistory(initialValue: T): History<T> {
    val history = remember(initialValue) {
        NavigationHistory(initialValue)
    }
    val pathState = remember(history) {
        mutableStateOf(history.currentValue)
    }
    return object : History<T> {
        override val currentValue = pathState.value
        override val index = history.index
        override val canGoBackward = history.canGoBackward
        override val canGoForward = history.canGoForward
        override fun moveBackward(): Boolean {
            val result = history.moveBackward()
            pathState.value = history.currentValue
            return result
        }
        override fun moveForward(): Boolean {
            val result = history.moveForward()
            pathState.value = history.currentValue
            return result
        }
        override fun push(value: T) {
            history.push(value)
            pathState.value = history.currentValue
        }
    }
}