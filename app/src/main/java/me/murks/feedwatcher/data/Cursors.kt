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