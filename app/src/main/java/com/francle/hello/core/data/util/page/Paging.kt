package com.francle.hello.core.data.util.page

interface Paging<Page, Item> {
    suspend fun loadNextItems()
    fun reset()
}
