package com.xhlab.multiplatform.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class UseCase<in Params, Result> : UseCaseExceptionHandler {

    protected abstract suspend fun execute(params: Params): Result

    suspend operator fun invoke(
        coroutineContext: CoroutineContext,
        params: Params,
        resultData: MutableStateFlow<Resource<Result>>
    ) {
        CoroutineScope(currentCoroutineContext()).launch(SupervisorJob()) {
            try {
                withContext(coroutineContext) {
                    resultData.value = Resource.success(execute(params))
                }
            } catch (e: Exception) {
                resultData.value = Resource.error(e)
                onException(e)
            }
        }
    }

    suspend operator fun invoke(
        coroutineContext: CoroutineContext,
        params: Params
    ): StateFlow<Resource<Result>> {
        val result = MutableStateFlow<Resource<Result>>(Resource.loading(null))
        invoke(coroutineContext, params, result)
        return result
    }

    suspend fun invokeInstant(params: Params): Resource<Result> {
        return try {
            Resource.success(execute(params))
        } catch (e: Exception) {
            onException(e)
            Resource.error(e)
        }
    }
}