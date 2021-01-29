package com.xhlab.multiplatform.ui

import com.xhlab.multiplatform.util.CommonFlow
import com.xhlab.multiplatform.util.asCommonFlow
import com.xhlab.multiplatform.util.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@InternalCoroutinesApi
actual open class ViewModel {
    actual val scope = CoroutineScope(dispatcher() + SupervisorJob())

    fun <T> toCommonFlow(flow: Flow<T>): CommonFlow<T> = flow.asCommonFlow()
    fun <T> toCommonFlow(flow: StateFlow<T>): CommonFlow<T> = flow.asCommonFlow()
}