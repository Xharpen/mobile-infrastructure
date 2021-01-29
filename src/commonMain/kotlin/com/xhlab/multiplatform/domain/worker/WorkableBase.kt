package com.xhlab.multiplatform.domain.worker

import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow

interface WorkableBase<in Params, Result> {
    fun run(params: Params, observer: MutableStateFlow<Resource<Result>?>)
    fun cancel()
}