package kr.sparkweb.multiplatform.paging

abstract class FinitePager<K : Any, V : Any> : Pager<K, V>() {
    abstract fun getLoadedCount(): Int
    abstract fun getTotalCount(): Int
}
