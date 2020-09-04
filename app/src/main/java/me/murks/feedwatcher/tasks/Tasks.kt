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

/**
 * Class with utility functions for creating tasks
 * @author zouroboros
 */
object Tasks {

    /**
     * Creates a task that maps the given parameter in the background.
     * @param mapper Function that maps items. This function in executed in the background.
     * @param consumer Action that consumes the results. This function is executed on the main thread.
     * @param errorHandler Action that is called in case an error occurs in the mapper function.
     * @param onFinished Action that is called when all items have been converted.
     */
    fun <TInput, TOutput> stream(mapper: (TInput) -> TOutput,
                                 consumer: (TOutput) -> Unit,
                                 errorHandler: (TInput, Exception) -> Unit,
                                 onFinished: () -> Unit): StreamingTask<TInput, TOutput> = StreamingTask(mapper, object : StreamingTask.Listener<TInput, TOutput> {
        override fun onResult(item: TOutput) = consumer(item)

        override fun onError(item: StreamingTask.Error<TInput>) = errorHandler(item.item, item.error)

        override fun onFinished() = onFinished()
    })

    /**
     * Creates a task for running a function in background
     * @param func Function to be run in the background.
     * @param consumer Action that consumes the result, executed on the main thread.
     * @param errorHandler Action that is called when an error occured running func.
     */
    fun <TInput, TOutput>run(func: (TInput) -> TOutput,
                             consumer: (TOutput) -> Unit,
                            errorHandler: (Exception) -> Unit) =
            object : AsyncTask<TInput, Void, Either<Exception, TOutput>>(){
                override fun doInBackground(vararg p0: TInput): Either<Exception, TOutput> {
                    try {
                        return Right(func(p0.get(0)))
                    } catch (e: Exception) {
                        return Left(e)
                    }
                }

                override fun onPostExecute(result: Either<Exception, TOutput>)
                        = result.either(errorHandler, consumer)
            }
}