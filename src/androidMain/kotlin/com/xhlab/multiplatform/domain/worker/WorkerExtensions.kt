package com.xhlab.multiplatform.domain.worker

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