package com.xhlab.multiplatform.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Special Flow for replacement of LiveEvent.
 */
class EventFlow<T> {
    private val channel = Channel<T>()

    val flow: Flow<T> = channel.receiveAsFlow()

    suspend fun emit(value: T) {
        channel.send(value)
    }
}