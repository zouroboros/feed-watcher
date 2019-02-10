package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import me.murks.feedwatcher.Either
import me.murks.feedwatcher.Left
import me.murks.feedwatcher.Right

/**
 * @author zouroboros
 */
class ActionTask<TResult>(private val f: () -> TResult,
                           listener: ErrorHandlingTaskListener<TResult, TResult, java.lang.Exception>) :
    AsyncTask<Void, TResult, Either<Exception, TResult>>() {

    private val _listener
            = ErrorHandlingTaskListenerWrapper(listener)

    override fun doInBackground(vararg args: Void): Either<Exception, TResult> {
        try {
            return Right(f())
        } catch (e: Exception) {
            return Left(e)
        }
    }

    override fun onProgressUpdate(vararg values: TResult) {
        for (value in values) {
            _listener.onProgress(value)
        }
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: Either<Exception, TResult>) {
        super.onPostExecute(result)
        _listener.onResult(result)
    }
}