package kr.sparkweb.multiplatform.domain.worker

import android.content.Context
import androidx.work.ForegroundInfo
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import kr.sparkweb.multiplatform.domain.MediatorUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kr.sparkweb.multiplatform.domain.worker.WorkerExceptionHandler

class MediatorWorkerFactory<in Params, Result, U : MediatorUseCase<Params, Result>>(
    private val dispatcher: CoroutineDispatcher,
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
            MediatorWorkableImpl.MediatorWorker::class.java.name ->
                if (workerParameters.tags.first { it != workerClassName } == tag) {
                    MediatorWorkableImpl.MediatorWorker(
                        appContext = appContext,
                        workerParameters = workerParameters,
                        dispatcher = dispatcher,
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
