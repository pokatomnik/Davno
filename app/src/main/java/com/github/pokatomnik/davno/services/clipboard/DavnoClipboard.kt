package com.github.pokatomnik.davno.services.clipboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pokatomnik.davno.services.storage.DavnoDavResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DavnoClipboard : ViewModel() {
    private val _clipboardContents = MutableStateFlow<ClipboardIntention?>(null)

    val empty
        get() = _clipboardContents
            .map { it == null }
            .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun cut(davResourcesToCut: List<DavnoDavResource>) {
        if (davResourcesToCut.isEmpty()) return
        _clipboardContents.value = ClipboardIntention(
            intentionId = ClipboardIntentionId.Cut,
            davResources = davResourcesToCut
        )
    }

    fun copy(davResourcesToCopy: List<DavnoDavResource>) {
        if (davResourcesToCopy.isEmpty()) return
        _clipboardContents.value = ClipboardIntention(
            intentionId = ClipboardIntentionId.Copy,
            davResources = davResourcesToCopy
        )
    }

    fun paste(
        ifNotEmpty: (
            intention: ClipboardIntentionId,
            davResourcesToPaste: List<DavnoDavResource>
        ) -> Unit
    ) {
        val contents = _clipboardContents.value ?: return
        val davResources = contents.davResources
        _clipboardContents.value = null
        ifNotEmpty(contents.intentionId, davResources)
    }
}
