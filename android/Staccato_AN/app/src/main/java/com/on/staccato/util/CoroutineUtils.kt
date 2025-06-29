package com.on.staccato.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun CoroutineScope.launchOnce(task: suspend () -> Unit): Job {
    val job =
        launch {
            task()
        }
    job.invokeOnCompletion {
        this.coroutineContext[Job]?.cancel()
    }
    return job
}
