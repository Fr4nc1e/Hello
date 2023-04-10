package com.francle.hello.core.data.page

import com.francle.hello.core.data.call.Resource
import kotlinx.coroutines.flow.Flow

class PagingManager<Page, Item>(
    initialPage: Page,
    private inline val onLoadUpdated: suspend (Boolean) -> Unit,
    private inline val onRequest: (nextPage: Page) -> Flow<Resource<List<Item?>?>>,
    private inline val onSuccess: suspend (items: Flow<Resource<List<Item?>?>>) -> Unit
) : Paging<Page, Item> {
    var currentPage = initialPage
    private var isMakingRequest = false
    override suspend fun loadNextItems() {
        if (isMakingRequest) return
        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentPage)
        isMakingRequest = false
        onSuccess(result)
        onLoadUpdated(false)
    }
}
