package kr.sparkweb.multiplatform.domain.worker

interface WorkerExceptionHandler {
    fun onException(throwable: Throwable)
}
