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
class WorkableImpl<in Params, Result, U : UseCase<Params, Result>> constructor(
    private val useCase: U
) : IOSWorkableBase<Params, Result>(), Workable<Params, Result, U> {

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