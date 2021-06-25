package com.xhlab.multiplatform.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transform

/**
 * Special Flow for replacement of LiveEvent.
 */
class EventFlow<T> {
    private val internalFlow = MutableStateFlow<EventWrapper<T>>(EventWrapper(null))

    val flow: Flow<T> = internalFlow.transform {
        if (it.wrappedValue != null) {
            emit(it.wrappedValue)
        }
    }

    fun emit(value: T) {
        internalFlow.value = EventWrapper(value)
    }

    private class EventWrapper<T>(val wrappedValue: T? = null) {
        override fun equals(other: Any?): Boolean {
            return false
        }

        override fun hashCode(): Int {
            return wrappedValue?.hashCode() ?: 0
        }
    }
}
