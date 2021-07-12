package com.xhlab.multiplatform.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

/**
 * Special Flow for replacement of LiveEvent.
 */
class EventFlow<T> {
    private val internalFlow = Channel<T>(Channel.CONFLATED)

    val flow: Flow<T> = internalFlow.receiveAsFlow()

    suspend fun emit(value: T) {
        internalFlow.send(value)
    }
}
