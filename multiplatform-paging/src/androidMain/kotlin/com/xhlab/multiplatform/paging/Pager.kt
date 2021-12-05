package com.xhlab.multiplatform.paging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.paging.Pager as AndroidXPager

actual abstract class Pager<K : Any, V : Any> {
    actual abstract val pagingData: Flow<PagingData<V>>

    actual abstract suspend fun <R : Any> map(
        transform: suspend (V) -> R
    ): Pager<K, R>
}

actual fun <K : Any, V : Any> createPager(
    clientScope: CoroutineScope,
    config: PagingConfig,
    initialKey: K,
    getItems: suspend (K, Int) -> PagingResult<K, V>
): Pager<K, V> {
    return PagerImpl(config, initialKey, getItems)
}

private class PagerImpl<K : Any, V : Any> : Pager<K, V> {

    override val pagingData: Flow<PagingData<V>>

    constructor(pagingData: Flow<PagingData<V>>) {
        this.pagingData = pagingData
    }

    constructor(
        config: PagingConfig,
        initialKey: K,
        getItems: suspend (K, Int) -> PagingResult<K, V>
    ) {
        pagingData = AndroidXPager(
            config = config,
            pagingSourceFactory = { PagingSource(initialKey, getItems) }
        ).flow
    }

    override suspend fun <R : Any> map(transform: suspend (V) -> R): Pager<K, R> {
        return PagerImpl(pagingData.map { flow -> flow.map { transform(it) } })
    }
}
