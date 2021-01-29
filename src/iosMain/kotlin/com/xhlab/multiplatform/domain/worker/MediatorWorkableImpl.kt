package com.xhlab.multiplatform.domain.worker

import com.xhlab.multiplatform.domain.MediatorUseCase
import com.xhlab.multiplatform.util.Resource
import com.xhlab.multiplatform.util.dispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first

@ExperimentalUnsignedTypes
class MediatorWorkableImpl<in Params, Result, U : MediatorUseCase<Params, Result>> constructor(
    private val useCase: U
) : IOSWorkableBase<Params, Result>(), MediatorWorkable<Params, Result, U> {

    private var job: Job? = null

    @InternalCoroutinesApi
    override fun runBackgroundTask(params: Params, observer: MutableStateFlow<Resource<Result>?>) {
        CoroutineScope(dispatcher()).launch {
            job = useCase.execute(CoroutineScope(currentCoroutineContext()), params)
            useCase.observe().first {
                observer.value = it
                it.status != Resource.Status.LOADING
            }
        }
    }

    override fun cancelBackgroundTask() {
        job?.cancel()
    }
}