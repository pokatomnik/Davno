package com.github.pokatomnik.davno.services.storage.filters

import com.thegrizzlylabs.sardineandroid.DavResource

interface WebdavResourceListFilter {
    fun filterWebdavResourceList(webdavResources: List<DavResource>): List<DavResource>
}