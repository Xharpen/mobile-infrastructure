package com.xhlab.multiplatform.domain.worker

interface DataConverter {
    fun <T> convert(from: T): Any?
    fun <T> convertBack(from: Any?): T
}