package com.github.pokatomnik.davno.services.db

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DavnoDatabaseViewModel @Inject constructor(
    val database: DavnoDatabase
) : ViewModel()