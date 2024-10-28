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

    sealed class ApiError : Exception() {
        object NetworkError : ApiError()
        object InvalidUrlError : ApiError()
        object ServerError : ApiError()
        data class UnknownError(override val message: String?) : ApiError()
    }

    suspend fun shortenUrl(originalUrl: String, path: String?): String {
        try {
            val request = ShortenUrlRequest(
                originalURL = originalUrl,
                path = path.takeIf { !it.isNullOrBlank() }
            )
            val response = api.shortenUrl(apiKey, request)
            return response.shortURL
        } catch (e: Exception) {
            throw when {
                e.message?.contains("Invalid URL") == true -> ApiError.InvalidUrlError
                e.message?.contains("network") == true -> ApiError.NetworkError
                e.message?.contains("500") == true -> ApiError.ServerError
                else -> ApiError.UnknownError(e.message)
            }
        }
    }

    suspend fun getUrls(): List<UrlData> {
        try {
            val response = api.getLinks(apiKey)
            return response.links.map { link ->
                UrlData(
                    originalUrl = link.originalURL,
                    shortPath = link.path,
                    shortenedUrl = link.shortURL
                )
            }
        } catch (e: Exception) {
            throw when {
                e.message?.contains("network") == true -> ApiError.NetworkError
                e.message?.contains("500") == true -> ApiError.ServerError
                else -> ApiError.UnknownError(e.message)
            }
        }
    }
}
