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
package me.murks.feedwatcher.data

import android.database.Cursor
import java.util.*

/**
 * Utility functions for {@see android.database.Cursor} objects
 * @author zouroboros
 */

/**
 * Reads the values of one column into a list
 */
fun <T> Cursor.getColumnValues(columnName: String, f: Function2<Cursor, Int, T>): Collection<T> {
    val values = LinkedList<T>()

    while (this.moveToNext()) {
        values.add(f(this, this.getColumnIndex(columnName)))
    }

    return values
}

fun Cursor.selectCount(): Int {
    this.moveToFirst()
    val count = this.getInt(0)
    this.close()
    return count;
}