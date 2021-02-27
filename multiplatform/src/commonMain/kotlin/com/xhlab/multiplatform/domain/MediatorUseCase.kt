package com.xhlab.multiplatform.domain

import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class MediatorUseCase<in Params, Result> : UseCaseExceptionHandler {

    private val result = MutableStateFlow<Resource<Result>>(Resource.loading(null))

    protected abstract suspend fun executeInternal(params: Params): Flow<Resource<Result>>

    suspend fun execute(dispatcher: CoroutineDispatcher, params: Params): Job {
        result.value = Resource.loading(null)
        return CoroutineScope(currentCoroutineContext()).launch(SupervisorJob()) {
            try {
                executeInternal(params)
                    .flowOn(dispatcher)
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