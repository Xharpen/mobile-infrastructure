package kr.sparkweb.multiplatform.util

data class Resource<out T>(val status: Status, val data: T?, val exception: Throwable?) {

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(e: Throwable?): Resource<T> {
            return Resource(Status.ERROR, null, e)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

        fun <T> Resource<T>.isSuccessful(): Boolean =
            (data != null || data is Unit) && (status == Status.SUCCESS)
    }

    enum class Status {
        ERROR, LOADING, SUCCESS;
    }
}
