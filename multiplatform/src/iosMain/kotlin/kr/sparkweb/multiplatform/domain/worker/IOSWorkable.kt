package kr.sparkweb.multiplatform.domain.worker

import kr.sparkweb.multiplatform.domain.UseCase
import kr.sparkweb.multiplatform.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kr.sparkweb.multiplatform.domain.worker.Workable
import kr.sparkweb.multiplatform.domain.worker.WorkerExceptionHandler

abstract class IOSWorkable<in Params, Result, U : UseCase<Params, Result>> constructor(
    private val useCase: U,
    workableListener: WorkableListener? = null
) : IOSWorkableBase<Params, Result>(workableListener),
    Workable<Params, Result, U>,
    WorkerExceptionHandler
{
    private var job: Job? = null

    override fun runBackgroundTask(
        scope: CoroutineScope,
        params: Params,
        observer: MutableStateFlow<Resource<Result>?>?
    ) {
        job = scope.launch {
            observer?.value = Resource.loading(null)
            val result = useCase.invokeInstant(params)
            observer?.value = result
        }
    }

    override fun cancelBackgroundTask() {
        job?.cancel()
    }
}
