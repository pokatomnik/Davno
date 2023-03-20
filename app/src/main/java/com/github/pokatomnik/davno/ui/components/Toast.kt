package com.github.pokatomnik.davno.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun makeToast(duration: Int = Toast.LENGTH_LONG): (message: String) -> Unit {
    val context = LocalContext.current
    return { message ->
        Toast.makeText(context, message, duration).show()
    }
}