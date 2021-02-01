package com.xhlab.multiplatform.domain.worker

import android.content.Context
import androidx.work.*
import com.xhlab.multiplatform.domain.UseCase
import com.xhlab.multiplatform.util.Resource

class WorkableImpl<in Params, Result, U : UseCase<Params, Result>> (
    workManager: WorkManager,
    existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
    tag: String,
    dataConverter: DataConverter,
    exceptionConverter: ExceptionConverter? = null
) : AndroidWorkableBase<Params, Result>(workManager, existingWorkPolicy, tag, dataConverter, exceptionConverter),
    Workable<Params, Result, U>
{
    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<Worker<Params, Result, U>>()
    }

    class Worker<in P, R, U : UseCase<P, R>> constructor(
        appContext: Context,
        workerParameters: WorkerParameters,
        private val useCase: U,
        private val exceptionHandler: WorkerExceptionHandler,
        private val converter: DataConverter,
        private val exceptionConverter: ExceptionConverter? = null
    ) : CoroutineWorker(appContext, workerParameters) {

        override suspend fun doWork(): Result {
            return try {
                val params = converter.convertBack<P>(inputData.keyValueMap[PARAMS])
                val result = useCase.invokeInstant(params)
                when (result.status) {
                    Resource.Status.SUCCESS ->
                        Result.success(workDataOf(DATA to result.data))
                    else ->
                        Result.failure(workDataOf(
                            EXCEPTION to exceptionConverter?.exceptionToString(result.exception)
                        ))
                }
            } catch (e: Exception) {
                exceptionHandler.onException(e)
                Result.failure(workDataOf(
                    EXCEPTION to exceptionConverter?.exceptionToString(e)
                ))
            }
        }
    }
}