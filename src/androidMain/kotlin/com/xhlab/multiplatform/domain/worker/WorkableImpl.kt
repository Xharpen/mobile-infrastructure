package com.xhlab.multiplatform.domain.worker

import android.content.Context
import androidx.work.*
import com.xhlab.multiplatform.domain.UseCase
import com.xhlab.multiplatform.util.Resource

class WorkableImpl<in Params, Result, U : UseCase<Params, Result>> (
    workManager: WorkManager,
    existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
    tag: String
) : AndroidWorkableBase<Params, Result>(workManager, existingWorkPolicy, tag),
    Workable<Params, Result, U>
{
    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<Worker<Params, Result, U>>()
    }

    class Worker<in P, R, U : UseCase<P, R>> constructor(
        appContext: Context,
        workerParameters: WorkerParameters,
        private val useCase: U,
        private val exceptionHandler: WorkerExceptionHandler
    ) : CoroutineWorker(appContext, workerParameters) {

        override suspend fun doWork(): Result {
            return try {
                @Suppress("unchecked_cast")
                val params = inputData.keyValueMap[PARAMS] as P
                val result = useCase.invokeInstant(params)
                when (result.status) {
                    Resource.Status.SUCCESS ->
                        Result.success(workDataOf(DATA to result.data))
                    else ->
                        Result.failure()
                }
            } catch (e: Throwable) {
                exceptionHandler.onException(e)
                Result.failure()
            }
        }
    }
}