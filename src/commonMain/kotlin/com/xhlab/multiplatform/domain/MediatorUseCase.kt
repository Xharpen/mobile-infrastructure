package com.xhlab.multiplatform.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.xhlab.multiplatform.util.Resource

abstract class MediatorUseCase<in Params, Result> : UseCaseExceptionHandler {

    private val result = MutableStateFlow<Resource<Result>>(Resource.loading(null))

    protected abstract suspend fun executeInternal(
        coroutineScope: CoroutineScope,
        params: Params
    ): Flow<Resource<Result>>

    fun execute(coroutineScope: CoroutineScope, params: Params) {
        coroutineScope.launch(SupervisorJob()) {
            try {
                executeInternal(this, params)
                    .collectLatest { result.value = it }
            } catch (e: Exception) {
                result.value = Resource.error(e)
                onException(e)
            }
        }
    }

    fun observe(): StateFlow<Resource<Result>> = result
}