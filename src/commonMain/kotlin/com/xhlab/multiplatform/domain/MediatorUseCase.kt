package com.xhlab.multiplatform.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.xhlab.multiplatform.util.Resource

abstract class MediatorUseCase<in Params, Result> {

    private val result = MutableStateFlow<Resource<Result>>(Resource.loading(null))

    protected abstract fun executeInternal(params: Params): StateFlow<Resource<Result>>

    protected abstract fun onExceptionWhileInvocation(e: Exception)

    fun execute(coroutineScope: CoroutineScope, params: Params) {
        coroutineScope.launch(SupervisorJob()) {
            try {
                executeInternal(params)
                    .collectLatest { result.value = it }
            } catch (e: Exception) {
                onExceptionWhileInvocation(e)
                result.value = Resource.error(e)
            }
        }
    }

    fun observe(): StateFlow<Resource<Result>> = result
}