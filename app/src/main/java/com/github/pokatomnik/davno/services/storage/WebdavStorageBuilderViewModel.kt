package com.github.pokatomnik.davno.services.storage

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebdavStorageBuilderViewModel @Inject constructor(val webdavStorageBuilder: WebdavStorageBuilder) : ViewModel()