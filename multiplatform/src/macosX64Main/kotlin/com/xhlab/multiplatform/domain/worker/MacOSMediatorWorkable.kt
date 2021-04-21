package com.xhlab.multiplatform.domain.worker

import com.xhlab.multiplatform.domain.MediatorUseCase
import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

abstract class MacOSMediatorWorkable<in Params, Result, U : MediatorUseCase<Params, Result>> constructor(
    private val dispatcher: CoroutineDispatcher,
    private val useCase: U
) : WorkableBase<Params, Result>,
    MediatorWorkable<Params, Result, U>,
    WorkerExceptionHandler
{
    private var job: Job? = null

    override fun run(
        scope: CoroutineScope,
        params: Params,
        observer: MutableStateFlow<Resource<Result>?>?
    ) {
        scope.launch {
            job = useCase.execute(dispatcher, params)
            useCase.observe().first {
                observer?.value = it
                it.status != Resource.Status.LOADING
            }
        }
    }

    override fun cancel() {
        job?.cancel()
    }
}