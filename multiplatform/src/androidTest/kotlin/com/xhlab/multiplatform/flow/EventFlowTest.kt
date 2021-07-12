package com.xhlab.multiplatform.flow

import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.multiplatform.util.MainCoroutineRule
import com.xhlab.multiplatform.util.runBlockingWithTimeout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class EventFlowTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Test
    fun fireSingleEvent() = runBlockingWithTimeout {
        val eventFlow = EventFlow<Unit>()
        eventFlow.fireAndCompare(Unit)
    }

    @Test
    fun fireTwoDifferentEvents() = runBlockingWithTimeout {
        val eventFlow = EventFlow<Int>()
        eventFlow.fireAndCompare(1)
        eventFlow.fireAndCompare(2)
    }

    @Test
    fun fireSameEventTwice() = runBlockingWithTimeout {
        val eventFlow = EventFlow<Unit>()
        eventFlow.fireAndCompare(Unit)
        eventFlow.fireAndCompare(Unit)
    }

    @Test
    fun fireNullEvent() = runBlockingWithTimeout {
        val eventFlow = EventFlow<String?>()
        eventFlow.fireAndCompare(null)
    }

    private suspend fun <T> EventFlow<T>.fireAndCompare(expected: T) {
        emit(expected)
        assertEquals(expected, flow.firstOrNull())
    }
}
