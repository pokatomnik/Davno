package com.github.pokatomnik.davno.services.usermessage

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessageDisplayerViewModel @Inject constructor(
    val messageDisplayer: MessageDisplayer
): ViewModel()