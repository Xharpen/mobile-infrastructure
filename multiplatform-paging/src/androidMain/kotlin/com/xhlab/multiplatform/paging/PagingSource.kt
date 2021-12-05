package com.xhlab.multiplatform.paging

import androidx.paging.PagingState

class PagingSource<K : Any, V : Any>(
    private val initialKey: K,
    private val getItems: suspend (K, Int) -> PagingResult<K, V>
) : androidx.paging.PagingSource<K, V>() {

    override val jumpingSupported: Boolean
        get() = true

    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<K, V>): K? {
        return null
    }

    override suspend fun load(params: LoadParams<K>): LoadResult<K, V> {
        val currentKey = params.key ?: initialKey
        return try {
            val pagingResult = getItems(currentKey, params.loadSize)
            LoadResult.Page(
                data = pagingResult.items,
                prevKey = if (currentKey == initialKey) null else pagingResult.prevKey(),
                nextKey = if (pagingResult.items.isEmpty()) null else pagingResult.nextKey()
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}
