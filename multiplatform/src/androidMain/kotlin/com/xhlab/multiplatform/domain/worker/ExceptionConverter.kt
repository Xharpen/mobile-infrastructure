package com.xhlab.multiplatform.domain.worker

interface ExceptionConverter {
    fun exceptionToString(t: Throwable?): String?
    fun exceptionFromString(s: String?): Throwable?
}