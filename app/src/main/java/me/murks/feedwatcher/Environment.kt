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
Copyright 2019-2020 Zouroboros
 */
package me.murks.feedwatcher

import me.murks.feedwatcher.data.DataStore
import java.io.Closeable

/**
 * @author zouroboros
 */
interface Environment: AutoCloseable {
    val dataStore: DataStore
    val settings: Settings
    val jobs: Jobs
    val notifications: Notifications
    val log: FeedwatcherLog
}