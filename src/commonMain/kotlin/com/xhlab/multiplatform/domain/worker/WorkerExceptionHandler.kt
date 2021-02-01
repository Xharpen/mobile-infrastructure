package com.xhlab.multiplatform.domain.worker

interface WorkerExceptionHandler {
    fun onException(throwable: Throwable)
    fun convertToString(throwable: Throwable?): String
}