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
Copyright 2019 Zouroboros
 */
package me.murks.feedwatcher

/**
 * Utility methods for texts
 * @author zouroboros
 */
object Texts {

    /**
     * Shortens a text to text prefix that is most maxLength long
     * @param text The string to shorten
     * @param maxLength the maximum length of the preview
     * @param suffix a suffix to be appended to the shortened string
     */
    fun preview(text: String, maxLength: Int, suffix: String): String {
        if(text.length <= maxLength) {
            return text
        }
        val lastSpace = text.substring(0, maxLength - suffix.length).lastIndexOf(" ")
        return text.substring(0, lastSpace) + suffix
    }
}