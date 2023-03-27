package com.github.pokatomnik.davno.services.clipboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thegrizzlylabs.sardineandroid.DavResource
import kotlinx.coroutines.flow.*

class DavnoClipboard : ViewModel() {
    private val _clipboardContents = MutableStateFlow<ClipboardIntention?>(null)

    val empty
        get() = _clipboardContents
            .map { it == null }
            .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun cut(davResourcesToCut: List<DavResource>) {
        if (davResourcesToCut.isEmpty()) return
        _clipboardContents.value = ClipboardIntention(
            intentionId = ClipboardIntentionId.Cut,
            davResources = davResourcesToCut
        )
    }

    fun copy(davResourcesToCopy: List<DavResource>) {
        if (davResourcesToCopy.isEmpty()) return
        _clipboardContents.value = ClipboardIntention(
            intentionId = ClipboardIntentionId.Copy,
            davResources = davResourcesToCopy
        )
    }

    fun paste(
        ifNotEmpty: (
            intention: ClipboardIntentionId,
            davResourcesToPaste: List<DavResource>
        ) -> Unit
    ) {
        val contents = _clipboardContents.value ?: return
        val davResources = contents.davResources
        _clipboardContents.value = null
        ifNotEmpty(contents.intentionId, davResources)
    }
}
