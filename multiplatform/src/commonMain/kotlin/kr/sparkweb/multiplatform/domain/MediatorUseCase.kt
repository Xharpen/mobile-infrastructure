package kr.sparkweb.multiplatform.domain

import kr.sparkweb.multiplatform.util.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class MediatorUseCase<in Params, Result> : UseCaseExceptionHandler {

    protected val result = MutableStateFlow<Resource<Result>>(Resource.loading(null))

    protected abstract suspend fun executeInternal(params: Params): Flow<Resource<Result>>

    suspend fun execute(dispatcher: CoroutineDispatcher, params: Params): Job {
        result.value = Resource.loading(null)
        return CoroutineScope(currentCoroutineContext()).launch(SupervisorJob()) {
            try {
                executeInternal(params)
                    .flowOn(dispatcher)
                    .collectLatest { result.value = it }
            } catch (e: Throwable) {
                result.value = Resource.error(e)
                if (e is CancellationException &&
                    this@MediatorUseCase is Cancellable
                ) {
                    onCancellation()
                } else {
                    onException(e)
                }
            }
        }
    }

    fun observe(): StateFlow<Resource<Result>> = result
}
