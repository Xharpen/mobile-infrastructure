package com.xhlab.multiplatform.domain

interface UseCaseExceptionHandler {
    fun onException(exception: Throwable)
}