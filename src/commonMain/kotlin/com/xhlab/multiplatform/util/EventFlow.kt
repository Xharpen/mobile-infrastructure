package com.xhlab.multiplatform.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Special Flow for replacement of LiveEvent.
 * May change in future
 */
class EventFlow<T> {

    private val _flow = MutableSharedFlow<T>(
        replay = 0,
        extraBufferCapacity = 0
    )

    val flow: Flow<T>
        get() = _flow

    suspend fun emit(value: T) {
        _flow.emit(value)
    }
}