package com.britannio.urlshort.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.britannio.urlshort.data.UrlData

class RealUrlApi(private val apiKey: String) {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.short.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ShortIoApi::class.java)

    suspend fun shortenUrl(originalUrl: String, path: String?): String {
        val request = ShortenUrlRequest(
            originalURL = originalUrl,
            path = path.takeIf { !it.isNullOrBlank() }
        )
        val response = api.shortenUrl(apiKey, request)
        return response.shortURL
    }

    suspend fun getUrls(): List<UrlData> {
        val response = api.getLinks(apiKey)
        return response.links.map { link ->
            UrlData(
                originalUrl = link.originalURL,
                shortenedUrl = link.shortURL
            )
        }
    }
}
