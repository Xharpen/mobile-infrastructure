package kr.sparkweb.multiplatform.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

fun <T> runBlockingWithTimeout(
    timeout: Long = 5000,
    block: suspend CoroutineScope.() -> T
): T = runBlocking {
    withTimeout(timeout) {
        block()
    }
}
