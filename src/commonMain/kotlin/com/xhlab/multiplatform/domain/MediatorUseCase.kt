package com.xhlab.multiplatform.domain

import kotlinx.coroutines.flow.*
import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.*

abstract class MediatorUseCase<in Params, Result> : UseCaseExceptionHandler {

    private val result = MutableStateFlow<Resource<Result>>(Resource.loading(null))

    protected abstract suspend fun executeInternal(params: Params): Flow<Resource<Result>>

    fun execute(coroutineScope: CoroutineScope, params: Params): Job {
        result.value = Resource.loading(null)
        return coroutineScope.launch(SupervisorJob()) {
            try {
                executeInternal(params)
                    .collectLatest { result.value = it }
            } catch (e: Exception) {
                result.value = Resource.error(e)
                if (e is CancellationException &&
                    this@MediatorUseCase is Cancellable) {
                    onCancellation()
                } else {
                    onException(e)
                }
            }
        }
    }

    fun observe(): StateFlow<Resource<Result>> = result
}