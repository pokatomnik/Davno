package com.github.pokatomnik.davno.services.clipboard

import com.thegrizzlylabs.sardineandroid.DavResource

data class ClipboardIntention(
    val intentionId: ClipboardIntentionId,
    val davResources: List<DavResource>
)