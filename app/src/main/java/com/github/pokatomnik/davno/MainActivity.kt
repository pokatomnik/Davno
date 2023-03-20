package com.github.pokatomnik.davno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.pokatomnik.davno.ui.theme.DavnoTheme
import dagger.hilt.android.AndroidEntryPoint

const val username: String = "pokatomnik@inbox.ru"
const val password: String = "tSeiMTxi3rLpt8cvLNNV"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DavnoTheme {
                AppComposable()
            }
        }
    }
}
