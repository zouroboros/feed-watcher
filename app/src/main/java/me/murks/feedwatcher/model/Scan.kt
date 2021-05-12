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
Copyright 2021 Zouroboros
 */
package me.murks.feedwatcher.model

import java.util.*

/**
 * Class represents a record about a scan of a feed.
 * @author zouroboros
 */
data class Scan(val feed: Feed, val sucessfully: Boolean, val error: String?, val scanDate: Date)
