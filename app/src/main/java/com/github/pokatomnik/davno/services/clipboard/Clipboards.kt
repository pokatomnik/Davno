package com.github.pokatomnik.davno.services.clipboard

class Clipboards {
    private val clipboards = mutableMapOf<Long, DavnoClipboard>()

    fun clipboardFor(vaultId: Long): DavnoClipboard {
        val clipboard = clipboards[vaultId]
        return clipboard ?: DavnoClipboard().apply {
            clipboards[vaultId] = this
        }
    }
}