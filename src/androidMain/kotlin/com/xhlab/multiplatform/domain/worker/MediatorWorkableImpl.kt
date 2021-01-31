package com.xhlab.multiplatform.domain.worker

import android.content.Context
import androidx.work.*
import com.xhlab.multiplatform.domain.MediatorUseCase
import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first

class MediatorWorkableImpl<in Params, Result, U : MediatorUseCase<Params, Result>> constructor(
    workManager: WorkManager,
    existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
    tag: String
) : AndroidWorkableBase<Params, Result>(workManager, existingWorkPolicy, tag),
    MediatorWorkable<Params, Result, U>
{
    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<MediatorWorker<Params, Result, U>>()
    }

    class MediatorWorker<in P, R, U : MediatorUseCase<P, R>> constructor(
        appContext: Context,
        workerParameters: WorkerParameters,
        private val dispatcher: CoroutineDispatcher,
        private val useCase: U,
        private val exceptionHandler: WorkerExceptionHandler
    ) : CoroutineWorker(appContext, workerParameters) {

        override suspend fun doWork(): Result {
            var job: Job? = null
            return try {
                @Suppress("unchecked_cast")
                val params = inputData.keyValueMap[PARAMS] as P
                job = useCase.execute(dispatcher, params)
                val resource = useCase.observe().first {
                    if (it.status == Resource.Status.LOADING) {
                        if (it.data != null) {
                            setProgressAsync(workDataOf(DATA to it.data))
                        }
                        false
                    } else {
                        true
                    }
                }
                when (resource.status) {
                    Resource.Status.SUCCESS ->
                        Result.success(workDataOf(DATA to resource.data))
                    else ->
                        Result.failure()
                }
            } catch (e: Throwable) {
                exceptionHandler.onException(e)
                job?.cancel()
                Result.failure()
            }
        }
    }
}
