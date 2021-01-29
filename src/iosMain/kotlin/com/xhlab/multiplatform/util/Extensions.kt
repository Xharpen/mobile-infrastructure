package com.xhlab.multiplatform.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> StateFlow<T>.asCommonFlow(): CommonFlow<T> = CommonFlow(this)
fun <T> Flow<T>.asCommonFlow(): CommonFlow<T> = CommonFlow(this)

class CommonFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    @InternalCoroutinesApi
    fun watch(block: (T) -> Unit) {
        onEach {
            block(it)
        }.launchIn(CoroutineScope(dispatcher()))
    }
}