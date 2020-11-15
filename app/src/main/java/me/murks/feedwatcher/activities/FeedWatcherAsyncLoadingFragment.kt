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
along with FeedWatcher. If not, see <https://www.gnu.org/licenses/>.
Copyright 2020 Zouroboros
 */
package me.murks.feedwatcher.activities

/**
 * Base class for fragments that load data asynchronously.
 * @author zouroboros
 */
abstract class FeedWatcherAsyncLoadingFragment<TResult>: FeedWatcherBaseFragment() {
    private val loadingThread = Thread {
        requireView().post { onLoadingStart() }
        loadData()
        requireView().post { onLoadingFinished() }
    }

    abstract fun loadData()

    abstract fun processResult(result: TResult)

    abstract fun onLoadingStart()

    abstract fun onLoadingFinished()

    protected fun reload() {
        if (!loadingThread.isAlive) {
            loadingThread.start()
        }
    }

    protected fun publishResult(result: TResult) {
        requireView().post { processResult(result) }
    }
}