package kr.sparkweb.multiplatform.domain

interface UseCaseExceptionHandler {
    fun onException(exception: Throwable)
}
