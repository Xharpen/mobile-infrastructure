package com.xhlab.multiplatform.domain.worker

import com.xhlab.multiplatform.domain.UseCase
import com.xhlab.multiplatform.util.Resource
import com.xhlab.multiplatform.util.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@ExperimentalUnsignedTypes
abstract class IOSWorkable<in Params, Result, U : UseCase<Params, Result>> constructor(
    private val useCase: U,
    workableListener: WorkableListener? = null
) : IOSWorkableBase<Params, Result>(workableListener),
    Workable<Params, Result, U>,
    WorkerExceptionHandler
{
    private var job: Job? = null

    @InternalCoroutinesApi
    override fun runBackgroundTask(params: Params, observer: MutableStateFlow<Resource<Result>?>) {
        job = CoroutineScope(dispatcher()).launch {
            observer.value = Resource.loading(null)
            observer.value = useCase.invokeInstant(params)
        }
    }

    override fun cancelBackgroundTask() {
        job?.cancel()
    }
}