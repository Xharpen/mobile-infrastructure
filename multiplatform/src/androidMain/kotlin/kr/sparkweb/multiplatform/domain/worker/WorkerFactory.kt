package kr.sparkweb.multiplatform.domain.worker

import android.content.Context
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import kr.sparkweb.multiplatform.domain.UseCase
import kr.sparkweb.multiplatform.domain.worker.WorkerExceptionHandler

class WorkerFactory<in Params, Result, U : UseCase<Params, Result>>(
    private val useCase: U,
    private val tag: String,
    private val exceptionHandler: WorkerExceptionHandler,
    private val inputConverter: DataConverter<Params>,
    private val outputConverter: DataConverter<Result>,
    private val exceptionConverter: ExceptionConverter? = null,
    private val foregroundInfo: ForegroundInfo? = null
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            WorkableImpl.Worker::class.java.name ->
                if (workerParameters.tags.first { it != workerClassName } == tag) {
                    WorkableImpl.Worker(
                        appContext = appContext,
                        workerParameters = workerParameters,
                        useCase = useCase,
                        exceptionHandler = exceptionHandler,
                        inputConverter = inputConverter,
                        outputConverter = outputConverter,
                        exceptionConverter = exceptionConverter,
                        foregroundInfo = foregroundInfo
                    )
                } else {
                    null
                }
            else -> null
        }
    }
}
