package com.github.pokatomnik.davno.services.storage.filters

import com.github.pokatomnik.davno.services.storage.fileExt
import com.thegrizzlylabs.sardineandroid.DavResource

class Markdown : WebdavResourceListFilter {
    override fun filterWebdavResourceList(
        webdavResources: List<DavResource>
    ) = webdavResources.filter { resource ->
        resource.isDirectory || resource.name.fileExt() == "md"
    }
}