package kr.sparkweb.multiplatform.domain.worker

interface WorkableListener {
    fun onRegisterBackgroundTask()
    fun onUnregisterBackgroundTask()
    fun onBackgroundTaskEnd()
}
