package com.xhlab.multiplatform.domain.worker

import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow

interface WorkableBase<in Params, Result> {
    fun run(scope: CoroutineScope, params: Params, observer: MutableStateFlow<Resource<Result>?>? = null)
    fun cancel()
}