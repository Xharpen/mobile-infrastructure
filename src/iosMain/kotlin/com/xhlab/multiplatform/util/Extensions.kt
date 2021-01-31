package com.xhlab.multiplatform.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

fun <T> StateFlow<T>.asCommonFlow(scope: CoroutineScope): CommonFlow<T> = CommonFlow(scope, this)
fun <T> Flow<T>.asCommonFlow(scope: CoroutineScope): CommonFlow<T> = CommonFlow(scope, this)

class CommonFlow<T>(
    private val scope: CoroutineScope,
    private val origin: Flow<T>
) : Flow<T> by origin {
    fun watch(block: (T) -> Unit) {
        onEach {
            block(it)
        }.launchIn(scope)
    }
}