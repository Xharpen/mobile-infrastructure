package kr.sparkweb.multiplatform.domain.worker

interface DataConverter<T> {
    fun convert(from: T?): Any?
    fun convertBack(from: Any?): T?
}
