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
Copyright 2020 - 2021 Zouroboros
 */
package me.murks.feedwatcher.atomrss

import org.xmlpull.v1.XmlPullParser
import java.util.*

/**
 * Class which implements a parser on top of a {@see XmlPullParser} to allows lazy parsing of xml streams.
 * @author zouroboros
 */
class LazyParser(private val parser: XmlPullParser, newNodes: Collection<ParserNode>) {
    val states = newNodes.associateBy { it.tag }
    val stack = Stack<ParserNode>()

    fun parseUntil(predicate: () -> Boolean) {
        while (parser.nextToken() != XmlPullParser.END_DOCUMENT && !predicate()) {
            if(parser.eventType == XmlPullParser.START_TAG) {
                if(stack.empty()) {
                    if(states.contains(parser.name)) {
                        stack.push(states[parser.name])
                    }
                } else {
                    if(stack.peek().subStates.containsKey(parser.name)) {
                        stack.push(stack.peek().subStates[parser.name])
                    }
                }
            }

            if(!stack.empty()) {
                stack.peek().action(parser)
            }

            if(parser.eventType == XmlPullParser.END_TAG) {
                if(!stack.empty() && stack.peek().tag == parser.name) {
                    stack.pop()
                }
            }
        }
    }
}