package pusan.university.plato_calendar.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * Simple in-memory CookieJar that stores cookies per host and allows reading a cookie value.
 * Lifetime is the app process lifetime.
 */
class InMemoryCookieStore @Inject constructor() : CookieJar {
    private val hostToCookies: MutableMap<String, MutableList<Cookie>> = ConcurrentHashMap()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isEmpty()) return
        val existing = hostToCookies.getOrPut(url.host) { mutableListOf() }
        cookies.forEach { newCookie ->
            existing.removeAll { it.name == newCookie.name && it.matches(url) }
            existing.add(newCookie)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val now = System.currentTimeMillis()
        val list = hostToCookies[url.host] ?: return emptyList()
        val valid = list.filter { cookie ->
            val expiresAt = cookie.expiresAt
            expiresAt == Long.MAX_VALUE || expiresAt > now
        }
        hostToCookies[url.host] = valid.toMutableList()

        return valid.filter { it.matches(url) }
    }

    fun getCookieValue(host: String, name: String): String? {
        val now = System.currentTimeMillis()
        val list = hostToCookies[host] ?: return null
        val valid = list.filter { cookie ->
            val expiresAt = cookie.expiresAt
            (expiresAt == Long.MAX_VALUE || expiresAt > now) && cookie.name == name
        }

        return valid.lastOrNull()?.value
    }
}
