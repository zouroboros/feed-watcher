package me.murks.feedwatcher.io

import org.xmlpull.v1.XmlPullParser
import java.util.*

/**
 * @author zouroboros
 */
class LazyParser(private val parser: XmlPullParser, newStates: Collection<ParserState>) {
    val states = newStates.associateBy { it.tag }
    val stack = Stack<ParserState>()

    fun parseUntil(predicate: () -> Boolean) {
        while (parser.next() != XmlPullParser.END_DOCUMENT && !predicate()) {
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