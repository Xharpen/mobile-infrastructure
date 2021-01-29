package com.xhlab.multiplatform.domain.worker

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.*
import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow

abstract class AndroidWorkableBase<in Params, Result> constructor(
    private val workManager: WorkManager,
    private val policy: ExistingWorkPolicy = ExistingWorkPolicy.REPLACE,
    private val tag: String
) : WorkableBase<Params, Result> {

    private var prevSource: LiveData<WorkInfo?>? = null
    private var observer: MutableStateFlow<Resource<Result>?>? = null
    private val workerObserver = Observer<WorkInfo?> {
        val status = it.getStatus()

        @Suppress("unchecked_cast")
        val data = when(status) {
            Resource.Status.LOADING -> it.progress.keyValueMap[DATA]
            Resource.Status.SUCCESS -> it.outputData.keyValueMap[DATA]
            else -> null
        } as Result
        observer?.value = Resource(status, data, null)

        if (status != Resource.Status.LOADING) {
            postRun()
        }
    }

    override fun run(params: Params, observer: MutableStateFlow<Resource<Result>?>) {
        // clear previous source to prevent npe or duplicated output
        removePrevSource()

        val input = when (params) {
            is Unit -> workDataOf()
            else -> workDataOf(PARAMS to params)
        }

        val request = getOneTimeWorkRequestBuilder()
            .setInputData(input)
            .addTag(tag)
            .build()
        workManager.enqueueUniqueWork(tag, policy, request)

        val source = workManager.getWorkInfoByIdLiveData(request.id)
        this.observer = observer
        source.observeForever(workerObserver)
        this.prevSource = source
    }

    override fun cancel() {
        workManager.cancelAllWorkByTag(tag)
    }

    abstract fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder

    private fun postRun() {
        removePrevSource()
        cancel()
    }

    private fun removePrevSource() {
        prevSource?.let { source ->
            source.removeObserver(workerObserver)
            observer = null
            prevSource = null
        }
    }

    companion object {
        internal const val PARAMS = "params"
        const val DATA = "data"
    }
}