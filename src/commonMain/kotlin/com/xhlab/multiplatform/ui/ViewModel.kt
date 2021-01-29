package com.xhlab.multiplatform.ui

import com.xhlab.multiplatform.domain.MediatorUseCase
import com.xhlab.multiplatform.domain.UseCase
import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

expect open class ViewModel constructor() {
    val scope: CoroutineScope
}

internal inline fun <reified P, R> ViewModel.invokeUseCase(
    useCase: UseCase<P, R>,
    params: P,
    resultData: MutableStateFlow<Resource<R>?>? = null
) = scope.launch {
    resultData?.value = Resource.loading(null)
    val result = useCase.invokeInstant(params)
    resultData?.value = result
}

internal inline fun <reified P, R> ViewModel.invokeMediatorUseCase(
    useCase: MediatorUseCase<P, R>,
    params: P
): Job {
    return useCase.execute(scope, params)
}
