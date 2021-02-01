package com.xhlab.multiplatform.domain.worker

import android.content.Context
import androidx.work.*
import com.xhlab.multiplatform.domain.UseCase
import com.xhlab.multiplatform.util.Resource

class WorkableImpl<in Params, Result, U : UseCase<Params, Result>> (
    workManager: WorkManager,
    existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
    tag: String,
    inputConverter: DataConverter<Params>,
    outputConverter: DataConverter<Result>,
    exceptionConverter: ExceptionConverter? = null
) : AndroidWorkableBase<Params, Result>(
        workManager = workManager,
        policy = existingWorkPolicy,
        tag = tag,
        inputConverter = inputConverter,
        outputConverter = outputConverter,
        exceptionConverter = exceptionConverter
    ),
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
        private val inputConverter: DataConverter<P>,
        private val outputConverter: DataConverter<R>,
        private val exceptionConverter: ExceptionConverter? = null
    ) : CoroutineWorker(appContext, workerParameters) {

        override suspend fun doWork(): Result {
            return try {
                val params = inputConverter.convertBack(inputData.keyValueMap[PARAMS])
                    ?: throw NullPointerException("params not provided.")
                val result = useCase.invokeInstant(params)
                when (result.status) {
                    Resource.Status.SUCCESS ->
                        Result.success(workDataOf(
                            DATA to outputConverter.convert(result.data)
                        ))
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