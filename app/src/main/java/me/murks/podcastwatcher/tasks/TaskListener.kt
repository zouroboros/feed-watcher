package me.murks.podcastwatcher.tasks

import me.murks.podcastwatcher.Either
import me.murks.podcastwatcher.Left
import me.murks.podcastwatcher.Right

/**
 * @author zouroboros
 */
interface TaskListener<TP, TR> {
    fun onProgress(progress: TP)
    fun onResult(result: TR)
}

interface ErrorHandlingTaskListener<TP, TR, TE> {
    fun onSuccessResult(result: TR)
    fun onErrorResult(error: TE)
    fun onProgress(progress: TP)
}

class ErrorHandlingTaskListenerWrapper<TP, TR, TE>(
        val listener: ErrorHandlingTaskListener<TP, TR, TE>) : TaskListener<TP, Either<TE, TR>> {

    override fun onProgress(progress: TP) {
        listener.onProgress(progress)
    }

    override fun onResult(result: Either<TE, TR>) {
        if(result.isLeft()) {
            listener.onErrorResult((result as Left).value)
        } else {
            listener.onSuccessResult((result as Right).value)
        }
    }
}