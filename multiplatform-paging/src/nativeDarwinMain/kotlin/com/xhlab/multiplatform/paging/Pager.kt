package com.xhlab.multiplatform.paging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

actual abstract class Pager<K : Any, V : Any> {
    actual abstract val pagingData: Flow<PagingData<V>>
    abstract fun loadInitial(initialLoadSize: Int)
    abstract fun loadNext(pageSize: Int)
    abstract fun loadNext()
    abstract fun refresh()

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
    return PagerImpl(clientScope, config, initialKey, getItems)
}

private class PagerImpl<K : Any, V : Any> : Pager<K, V> {

    private val loader: PagerLoader<K, V>
    override val pagingData: Flow<PagingData<V>>
        get() = loader.pagingData

    private constructor(loader: PagerLoader<K, V>) {
        this.loader = loader
    }

    constructor(
        clientScope: CoroutineScope,
        config: PagingConfig,
        initialKey: K,
        getItems: suspend (K, Int) -> PagingResult<K, V>
    ) {
        loader = PagerLoader(clientScope, config, initialKey, getItems)
        loadInitial(config.initialLoadSize)
    }

    override fun loadInitial(initialLoadSize: Int) {
        loader.loadItems(initialLoadSize)
    }

    override fun loadNext(pageSize: Int) {
        loader.loadItems(pageSize)
    }

    override fun loadNext() {
        loader.loadItems()
    }

    override fun refresh() {
        // TODO: support crude refresh
    }

    override suspend fun <R : Any> map(transform: suspend (V) -> R): Pager<K, R> {
        return PagerImpl(loader.map { transform(it) })
    }
}

private class PagerLoader<K : Any, V : Any> constructor(
    private val clientScope: CoroutineScope,
    private val config: PagingConfig,
    private val initialKey: K,
    private val getItems: suspend (K, Int) -> PagingResult<K, V>
) {

    private val _pagingData = MutableStateFlow<PagingData<V>>(PagingData(emptyList()))
    val pagingData: Flow<PagingData<V>> = _pagingData

    private val currentPagingResult = MutableStateFlow<PagingResult<K, V>?>(null)

    private val _hasNextPage = MutableStateFlow(true)
    private val hasNextPage: Boolean
        get() = _hasNextPage.value

    fun loadItems() {
        loadItems(config.pageSize)
    }

    fun loadItems(size: Int) {
        val pagingResult = currentPagingResult.value
        val key = if (pagingResult == null) {
            initialKey
        } else {
            pagingResult.nextKey()
        }

        if (key != null && hasNextPage) {
            clientScope.launch {
                val newPagingResult = getItems(key, size)
                _pagingData.value = _pagingData.value.toMutableList().apply {
                    addAll(newPagingResult.items)
                }.toPagingData()
                _hasNextPage.value = newPagingResult.items.size >= config.pageSize
                currentPagingResult.value = newPagingResult
            }
        }
    }

    suspend fun <R : Any> map(transform: suspend (V) -> R): PagerLoader<K, R> {
        return PagerLoader(
            clientScope = clientScope,
            config = config,
            initialKey = initialKey,
            getItems = { key, page ->
                getItems(key, page).map { transform(it) }
            }
        )
    }
}
