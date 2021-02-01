package com.xhlab.multiplatform.domain.worker

interface ExceptionConverter {
    fun exceptionToString(t: Exception?): String?
    fun exceptionFromString(s: String?): Exception?
}