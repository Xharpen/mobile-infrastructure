package com.xhlab.multiplatform.util

import kotlinx.coroutines.*
import platform.darwin.*
import kotlin.coroutines.CoroutineContext

@InternalCoroutinesApi
actual fun dispatcher(): CoroutineDispatcher = MainLoopDispatcher

@OptIn(InternalCoroutinesApi::class)
private object MainLoopDispatcher: CoroutineDispatcher(), Delay {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatch_get_main_queue()) {
            try {
                block.run()
            } catch (err: Throwable) {
                print("UNCAUGHT ${err.message ?: ""}")
                throw err
            }
        }
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, timeMillis * 1_000_000), dispatch_get_main_queue()) {
            try {
                with(continuation) {
                    resumeUndispatched(Unit)
                }
            } catch (err: Throwable) {
                print("UNCAUGHT ${err.message ?: ""}")
                throw err
            }
        }
    }

    @InternalCoroutinesApi
    override fun invokeOnTimeout(
        timeMillis: Long,
        block: Runnable,
        context: CoroutineContext
    ): DisposableHandle {
        val handle = object : DisposableHandle {
            var disposed = false
                private set

            override fun dispose() {
                disposed = true
            }
        }
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, timeMillis * 1_000_000), dispatch_get_main_queue()) {
            try {
                if (!handle.disposed) {
                    block.run()
                }
            } catch (err: Throwable) {
                print("UNCAUGHT ${err.message ?: ""}")
                throw err
            }
        }

        return handle
    }
}