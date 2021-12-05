package com.xhlab.multiplatform.paging

actual class PagingData<T : Any> constructor(items: List<T>) : List<T> by items

fun <T: Any> List<T>.toPagingData(): PagingData<T> = PagingData(this)

actual suspend fun <T : Any, R : Any> PagingData<T>.map(transform: suspend (T) -> R): PagingData<R> {
    return this.toMutableList().map { transform(it) } as PagingData<R>
}

actual suspend fun <T : Any, R : Any> PagingData<T>.flatMap(transform: suspend (T) -> Iterable<R>): PagingData<R> {
    return this.toMutableList().flatMap { transform(it) } as PagingData<R>
}

actual suspend fun <T : Any> PagingData<T>.filter(predicate: suspend (T) -> Boolean): PagingData<T> {
    return this.toMutableList().filter { predicate(it) } as PagingData<T>
}
