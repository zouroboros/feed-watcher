package me.murks.feedwatcher.io

import org.xmlpull.v1.XmlPullParser

/**
 * @author zouroboros
 */
class ParserState(val tag: String, val action: (parser: XmlPullParser) -> Unit, newSubStates: Collection<ParserState> = listOf()) {
    val subStates = newSubStates.associateBy({ it.tag })
}

