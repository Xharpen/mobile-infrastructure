package com.xhlab.multiplatform.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.xhlab.multiplatform.util.Resource

abstract class UseCase<in Params, Result> {

    protected abstract suspend fun execute(params: Params): Result

    protected abstract suspend fun onExceptionWhileInvocation(e: Exception)

    operator fun invoke(
        coroutineScope: CoroutineScope,
        params: Params,
        resultData: MutableStateFlow<Resource<Result>>
    ) {
        coroutineScope.launch(SupervisorJob()) {
            try {
                resultData.value = Resource.success(execute(params))
            } catch (e: Exception) {
                onExceptionWhileInvocation(e)
                resultData.value = Resource.error(e)
            }
        }
    }

    operator fun invoke(coroutineScope: CoroutineScope, params: Params): StateFlow<Resource<Result>> {
        val result = MutableStateFlow<Resource<Result>>(Resource.loading(null))
        invoke(coroutineScope, params, result)
        return result
    }

    suspend fun invokeInstant(params: Params): Resource<Result> {
        return try {
            Resource.success(execute(params))
        } catch (e: Exception) {
            onExceptionWhileInvocation(e)
            Resource.error(e)
        }
    }
}