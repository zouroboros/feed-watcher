package me.murks.feedwatcher

import org.jsoup.Jsoup

/**
 * @author zouroboros
 */
object HtmlTags {
    fun text(html: String): String {
        return Jsoup.parseBodyFragment(html).text()
    }

    fun wrapInDocument(html: String, backgroundColor: Int): String {
        val document = Jsoup.parse(html)
        val color = cssColor(backgroundColor)
        document.body().attr("style",
                "padding: 0; margin: 0; background-color: $color;")
        return document.html()
    }

    private fun cssColor(color: Int): String {
        val A = color shr 24 and 0xff // or color >>> 24
        val R = color shr 16 and 0xff
        val G = color shr 8 and 0xff
        val B = color and 0xff
        return "rgba($R, $G, $B, $A);"
    }
}