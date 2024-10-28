package com.britannio.urlshort.api

import com.britannio.urlshort.data.UrlData

class MockUrlApi {
    private val baseUrl = "https://short.url/"
    
    fun shortenUrl(originalUrl: String, customPath: String? = null): UrlData {
        val path = customPath ?: generateRandomString(6)
        return UrlData(
            originalUrl = originalUrl,
            shortPath = path,
            shortenedUrl = baseUrl + path
        )
    }

    fun getRecentUrls(): List<UrlData> {
        return listOf(
            UrlData(originalUrl = "https://example.com", shortPath = "ex1", shortenedUrl = "https://short.url/ex1"),
            UrlData(originalUrl = "https://google.com", shortPath = "ggl", shortenedUrl = "https://short.url/ggl")
        )
    }

    private fun generateRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
