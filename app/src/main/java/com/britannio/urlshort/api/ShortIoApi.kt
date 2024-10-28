package com.britannio.urlshort.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header

interface ShortIoApi {
    @POST("links")
    suspend fun shortenUrl(
        @Header("Authorization") apiKey: String,
        @Body request: ShortenUrlRequest
    ): ShortenUrlResponse
}

data class ShortenUrlRequest(
    val domain: String = "britann.io",
    val originalURL: String,
    val path: String?
)

data class ShortenUrlResponse(
    val originalURL: String,
    val shortURL: String
)
