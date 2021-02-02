package com.xhlab.multiplatform.domain.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkInfo
import com.xhlab.multiplatform.util.Resource

internal fun WorkInfo.getStatus(): Resource.Status {
    val isFinished = state.isFinished
    return  when {
        isFinished && state == WorkInfo.State.SUCCEEDED ->
            Resource.Status.SUCCESS
        isFinished ->
            Resource.Status.ERROR
        else ->
            Resource.Status.LOADING
    }
}

fun Context.createForegroundInfo(
    channelId: String,
    notificationId: Int,
    notification: Notification
): ForegroundInfo {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createChannel(this, channelId)
    }
    return ForegroundInfo(notificationId, notification)
}

@RequiresApi(Build.VERSION_CODES.O)
internal fun createChannel(
    context: Context,
    channelId: String,
    channelName: String = "channel",
    importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    channelBuilder: (NotificationChannel) -> Unit = { channel ->
        channel.setSound(null, null)
        channel.importance = NotificationManager.IMPORTANCE_LOW
    }
) {
    val channel = NotificationChannel(channelId, channelName, importance).apply {
        channelBuilder(this)
    }
    NotificationManagerCompat.from(context).createNotificationChannel(channel)
}
