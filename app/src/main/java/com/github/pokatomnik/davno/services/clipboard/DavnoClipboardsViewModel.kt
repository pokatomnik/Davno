package com.github.pokatomnik.davno.services.clipboard

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DavnoClipboardsViewModel @Inject constructor(
    val clipboards: Clipboards
) : ViewModel()