package com.britannio.urlshort.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    suspend fun getUrls(): List<String> {
        // This will be implemented later
        return emptyList()
    }
}
