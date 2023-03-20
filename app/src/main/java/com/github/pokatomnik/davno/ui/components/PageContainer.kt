package com.github.pokatomnik.davno.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val HEADER_HEIGHT = 56

@Composable
fun PageContainer(
    priorButton: (@Composable () -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    trailingButton: (@Composable () -> Unit)? = null,
    body: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.surface)
    ) {
        if (header != null) {
            Surface(
                color = MaterialTheme.colors.primarySurface,
                contentColor = contentColorFor(MaterialTheme.colors.primarySurface),
                elevation = BottomNavigationDefaults.Elevation,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(HEADER_HEIGHT.dp)
                        .padding(horizontal = 8.dp),
                ) {
                    if (priorButton != null) {
                        Column(
                            modifier = Modifier.width(HEADER_HEIGHT.dp).fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            priorButton()
                        }
                    } else {
                        Spacer(modifier = Modifier.width(HEADER_HEIGHT.dp).fillMaxHeight())
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        header()
                    }
                    if (trailingButton != null) {
                        Column(
                            modifier = Modifier.width(HEADER_HEIGHT.dp).fillMaxHeight(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            trailingButton()
                        }
                    } else {
                        Spacer(modifier = Modifier.width(HEADER_HEIGHT.dp).fillMaxHeight())
                    }
                }
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            body()
        }
    }
}