package com.xhlab.multiplatform.domain.worker

import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import platform.UIKit.UIApplication
import platform.UIKit.UIBackgroundTaskIdentifier
import platform.UIKit.UIBackgroundTaskInvalid

@ExperimentalUnsignedTypes
abstract class IOSWorkableBase<in Params, Result>(
    private var listener: WorkableListener? = null
) : WorkableBase<Params, Result> {

    private var backgroundTask: UIBackgroundTaskIdentifier? = null

    private val backgroundManager = BackgroundManager {
        if (backgroundTask == UIBackgroundTaskInvalid) {
            registerBackgroundTask()
        }
    }

    override fun run(params: Params, observer: MutableStateFlow<Resource<Result>?>) {
        backgroundManager.registerBackgroundTaskManager()
        registerBackgroundTask()
        runBackgroundTask(params, observer)
    }

    override fun cancel() {
        backgroundManager.unregisterBackgroundTaskManager()
        cancelBackgroundTask()
    }

    abstract fun runBackgroundTask(
        params: Params,
        observer: MutableStateFlow<Resource<Result>?>
    )

    abstract fun cancelBackgroundTask()

    fun setWorkableListener(listener: WorkableListener) {
        this.listener = listener
        backgroundManager.setListener(listener)
    }

    private fun registerBackgroundTask() {
        listener?.onRegisterBackgroundTask()
        backgroundTask = UIApplication.sharedApplication.beginBackgroundTaskWithExpirationHandler {
            endBackgroundTask()
            backgroundManager.unregisterBackgroundTaskManager()
        }
    }

    private fun endBackgroundTask() {
        listener?.onBackgroundTaskEnd()
        backgroundTask?.let {
            UIApplication.sharedApplication.endBackgroundTask(it)
        }
        backgroundTask = UIBackgroundTaskInvalid
    }
}