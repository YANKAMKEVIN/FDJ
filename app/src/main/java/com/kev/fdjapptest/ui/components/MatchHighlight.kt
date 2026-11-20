package com.kev.fdjapptest.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/** Range of the first case-insensitive occurrence of [query] in [text], or null. */
fun matchRange(text: String, query: String): IntRange? {
    val q = query.trim()
    if (q.isEmpty()) return null
    val start = text.indexOf(q, ignoreCase = true)
    return if (start < 0) null else start until start + q.length
}

/** [text] with the part matching [query] in bold. */
fun highlightMatch(text: String, query: String): AnnotatedString {
    val range = matchRange(text, query) ?: return AnnotatedString(text)
    return buildAnnotatedString {
        append(text.substring(0, range.first))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append(text.substring(range.first, range.last + 1))
        }
        append(text.substring(range.last + 1))
    }
}
