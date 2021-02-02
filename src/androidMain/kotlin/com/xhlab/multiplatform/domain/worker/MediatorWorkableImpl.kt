package com.xhlab.multiplatform.domain.worker

import android.app.NotificationManager
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
    tag: String,
    inputConverter: DataConverter<Params>,
    outputConverter: DataConverter<Result>,
    exceptionConverter: ExceptionConverter
) : AndroidWorkableBase<Params, Result>(
        workManager = workManager,
        policy = existingWorkPolicy,
        tag = tag,
        inputConverter = inputConverter,
        outputConverter = outputConverter,
        exceptionConverter = exceptionConverter
    ),
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
        private val exceptionHandler: WorkerExceptionHandler,
        private val inputConverter: DataConverter<P>,
        private val outputConverter: DataConverter<R>,
        private val exceptionConverter: ExceptionConverter? = null,
        private val foregroundInfo: ForegroundInfo? = null
    ) : CoroutineWorker(appContext, workerParameters) {

        private val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        override suspend fun doWork(): Result {
            var job: Job? = null
            if (foregroundInfo != null) {
                setForeground(foregroundInfo)
            }
            return try {
                val params = inputConverter.convertBack(inputData.keyValueMap[PARAMS])
                    ?: throw NullPointerException("params not provided.")
                job = useCase.execute(dispatcher, params)
                val resource = useCase.observe().first {
                    if (it.status == Resource.Status.LOADING) {
                        if (it.data != null) {
                            setProgressAsync(workDataOf(
                                DATA to outputConverter.convert(it.data)
                            ))
                        }
                        false
                    } else {
                        true
                    }
                }
                when (resource.status) {
                    Resource.Status.SUCCESS ->
                        Result.success(workDataOf(
                            DATA to outputConverter.convert(resource.data)
                        ))
                    else ->
                        Result.failure(workDataOf(
                            EXCEPTION to exceptionConverter?.exceptionToString(resource.exception)
                        ))
                }
            } catch (e: Throwable) {
                exceptionHandler.onException(e)
                job?.cancel()
                Result.failure(workDataOf(
                    EXCEPTION to exceptionConverter?.exceptionToString(e)
                ))
            } finally {
                if (foregroundInfo != null) {
                    notificationManager.cancel(foregroundInfo.notificationId)
                }
            }
        }
    }
}
