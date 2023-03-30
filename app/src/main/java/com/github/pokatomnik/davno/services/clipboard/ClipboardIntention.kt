package com.github.pokatomnik.davno.services.clipboard

import com.github.pokatomnik.davno.services.storage.DavnoDavResource

data class ClipboardIntention(
    val intentionId: ClipboardIntentionId,
    val davResources: List<DavnoDavResource>
)