package com.github.pokatomnik.davno.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T : Any> rememberLastNonNull(nullableValue: T?): T? {
    val (value, setValue) = remember {
        mutableStateOf(nullableValue)
    }

    LaunchedEffect(nullableValue) {
        if (nullableValue != null) setValue(nullableValue)
    }

    return value
}