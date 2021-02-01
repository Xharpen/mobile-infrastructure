package com.xhlab.multiplatform.domain.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.xhlab.multiplatform.domain.MediatorUseCase
import kotlinx.coroutines.CoroutineDispatcher

class MediatorWorkerFactory<in Params, Result, U : MediatorUseCase<Params, Result>>(
    private val dispatcher: CoroutineDispatcher,
    private val useCase: U,
    private val tag: String,
    private val exceptionHandler: WorkerExceptionHandler,
    private val dataConverter: DataConverter,
    private val exceptionConverter: ExceptionConverter? = null
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
                        dataConverter = dataConverter,
                        exceptionConverter = exceptionConverter
                    )
                } else {
                    null
                }
            else -> null
        }
    }
}
