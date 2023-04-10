package com.francle.hello.core.data.page

interface Paging<Page, Item> {
    suspend fun loadNextItems()
}
