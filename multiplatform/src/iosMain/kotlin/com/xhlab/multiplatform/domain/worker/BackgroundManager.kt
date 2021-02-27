package com.xhlab.multiplatform.domain.worker

import kotlinx.cinterop.ExportObjCClass
import kotlinx.cinterop.ObjCAction
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.darwin.NSObject
import platform.objc.sel_registerName

@ExportObjCClass
class BackgroundManager(
    private var listener: WorkableListener? = null,
    private val reinstateBackgroundTask: () -> Unit
) : NSObject() {

    fun registerBackgroundTaskManager() {
        listener?.onRegisterBackgroundTask()
        NSNotificationCenter.defaultCenter.addObserver(
            observer = this,
            selector = sel_registerName("reinstateBackgroundTask"),
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null
        )
    }

    fun unregisterBackgroundTaskManager() {
        listener?.onUnregisterBackgroundTask()
        NSNotificationCenter.defaultCenter.removeObserver(this)
    }

    fun setListener(listener: WorkableListener) {
        this.listener = listener
    }

    @ObjCAction
    @Suppress("unused")
    @ExperimentalUnsignedTypes
    private fun reinstateBackgroundTask() {
        reinstateBackgroundTask.invoke()
    }
}