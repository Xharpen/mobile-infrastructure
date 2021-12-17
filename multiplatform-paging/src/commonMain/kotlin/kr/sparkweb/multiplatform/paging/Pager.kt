package kr.sparkweb.multiplatform.paging

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

expect abstract class Pager<K : Any, V : Any> {
    abstract val pagingData: Flow<PagingData<V>>

    actual abstract suspend fun <R : Any> map(
        transform: suspend (V) -> R
    ): Pager<K, R>
}

expect fun <K : Any, V : Any> createPager(
    clientScope: CoroutineScope,
    config: PagingConfig,
    initialKey: K,
    getItems: suspend (K, Int) -> PagingResult<K, V>
): Pager<K, V>
