package com.xhlab.multiplatform.paging

data class PagingResult<K, V>(
    val items: List<V>,
    val currentKey: K,
    val prevKey: () -> K?,
    val nextKey: () -> K?
) {
    inline fun <R> map(transform: (V) -> R): PagingResult<K, R> {
        return PagingResult(
            items = items.map { transform(it) },
            currentKey = currentKey,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}
