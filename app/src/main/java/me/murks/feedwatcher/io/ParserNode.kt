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
package me.murks.feedwatcher.io

import org.xmlpull.v1.XmlPullParser

/**
 * Class that represents a node in the in the definition of a hierarchical parser.
 * @author zouroboros
 */
class ParserNode(val tag: String, val action: (parser: XmlPullParser) -> Unit, newSubNodes: Collection<ParserNode> = listOf()) {
    val subStates = newSubNodes.associateBy({ it.tag })
}

