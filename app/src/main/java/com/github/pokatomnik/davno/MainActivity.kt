package com.github.pokatomnik.davno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.pokatomnik.davno.services.storage.ConnectionParams
import com.github.pokatomnik.davno.services.storage.rememberWebdavStorage
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
