package pusan.university.plato_calendar.data.util.parser

import org.jsoup.Jsoup

internal fun String.parseAnchor(): String {
    val document = Jsoup.parse(this)
    return document
        .selectFirst("input[name=anchor]")
        ?.attr("value")
        ?.trim()
        .orEmpty()
}

internal fun String.parseLoginToken(): String? {
    val document = Jsoup.parse(this)
    val token =
        document
            .selectFirst("input[name=logintoken]")
            ?.attr("value")
            ?.trim()

    return token?.takeIf { it.isNotBlank() }
}

internal fun String.parseLoginTab(): String {
    val document = Jsoup.parse(this)
    return document
        .selectFirst("input[name=logintab]")
        ?.attr("value")
        ?.trim()
        .orEmpty()
}
