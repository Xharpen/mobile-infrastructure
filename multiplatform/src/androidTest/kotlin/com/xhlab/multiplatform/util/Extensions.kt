package com.xhlab.multiplatform.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.CoroutineContext

fun <T> runBlockingWithTimeout(
    timeout: Long = 5000,
    block: suspend CoroutineScope.() -> T
): T = runBlocking {
    withTimeout(timeout) {
        block()
    }
}
