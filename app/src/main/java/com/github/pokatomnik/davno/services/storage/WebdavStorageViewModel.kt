package com.github.pokatomnik.davno.services.storage

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebdavStorageViewModel @Inject constructor(
    val webdavStorageBuilder: WebdavStorageBuilder
) : ViewModel()