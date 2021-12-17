package kr.sparkweb.multiplatform.paging

actual class PagingConfig actual constructor(
    val pageSize: Int,
    val prefetchDistance: Int,
    val enablePlaceholders: Boolean,
    val initialLoadSize: Int,
    val maxSize: Int,
    val jumpThreshold: Int
)
