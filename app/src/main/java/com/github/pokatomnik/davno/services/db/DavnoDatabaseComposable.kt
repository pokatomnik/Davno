package com.github.pokatomnik.davno.services.db

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun rememberDavnoDatabase(): DavnoDatabase {
    return hiltViewModel<DavnoDatabaseViewModel>().database
}