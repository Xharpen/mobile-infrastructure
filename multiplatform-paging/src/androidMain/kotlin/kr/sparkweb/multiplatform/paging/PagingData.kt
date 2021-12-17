package kr.sparkweb.multiplatform.paging

import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.flatMap
import androidx.paging.map

actual typealias PagingData<T> = PagingData<T>

actual suspend fun <T : Any, R : Any> PagingData<T>.map(transform: suspend (T) -> R): PagingData<R> {
    return map(transform)
}

actual suspend fun <T : Any, R : Any> PagingData<T>.flatMap(transform: suspend (T) -> Iterable<R>): PagingData<R> {
    return flatMap(transform)
}

actual suspend fun <T : Any> PagingData<T>.filter(predicate: suspend (T) -> Boolean): PagingData<T> {
    return filter(predicate)
}

