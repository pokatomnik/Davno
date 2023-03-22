package com.github.pokatomnik.davno.screens.vaultview

data class DavResourceState<T : Any>(
    val isLoading: Boolean,
    val errorText: String?,
    val data: T
)