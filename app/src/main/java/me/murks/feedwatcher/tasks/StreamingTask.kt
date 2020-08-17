/*
This file is part of FeedWatcher.

FeedWatcher is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

FeedWatcher is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with FeedWatcher.  If not, see <https://www.gnu.org/licenses/>.
Copyright 2020 Zouroboros
 */
package me.murks.feedwatcher.tasks

import android.os.AsyncTask
import me.murks.feedwatcher.Either
import me.murks.feedwatcher.Left
import me.murks.feedwatcher.Right
import java.lang.Exception

/**
 * Task for processing lists.
 *
 * @author zouroboros
 *
 * @param func The function that processes a list item.
 * @param listener The listener that processes the result.
 */
class StreamingTask<TInput, TOutput>(private val func: (input: TInput) -> TOutput,
                                     private val listener: Listener<TInput, TOutput>) :
        AsyncTask<TInput, Either<StreamingTask.Error<TInput>, TOutput>, Unit>() {

    /**
     * Class that hold an error and the item that was processed when the error occured.
     */
    class Error<TInput>(val item: TInput, val error: Exception)

    /***
     * Interface for listener that process the results.
     */
    interface Listener<TInput, TOutput> {
        fun onResult(item: TOutput)
        fun onError(item: Error<TInput>)
        fun onFinished()
    }

    override fun doInBackground(vararg inputs: TInput) {
        inputs.forEach {
            try {
                publishProgress(Right(func(it)))
            } catch (e: Exception) {
               publishProgress(Left(Error(it, e)))
            }
        }
    }

    override fun onProgressUpdate(vararg values: Either<Error<TInput>, TOutput>) {
        for (value in values) {
            when(value) {
                is Left -> listener.onError(value.value)
                is Right -> listener.onResult(value.value)
            }
        }
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(result: Unit?) {
        listener.onFinished()
        super.onPostExecute(result)
    }
}