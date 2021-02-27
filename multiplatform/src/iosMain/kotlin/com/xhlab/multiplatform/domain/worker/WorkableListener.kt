package com.xhlab.multiplatform.domain.worker

interface WorkableListener {
    fun onRegisterBackgroundTask()
    fun onUnregisterBackgroundTask()
    fun onBackgroundTaskEnd()
}