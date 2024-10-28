package com.britannio.urlshort.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.britannio.urlshort.data.UrlData
import okhttp3.OkHttpClient
import okhttp3.Interceptor

class RealUrlApi(private val apiKey: String) {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", apiKey)
                .addHeader("accept", "application/json")
                .build()
            chain.proceed(request)
        })
        .build()

    private val api = Retrofit.Builder()
        .baseUrl("https://api.short.io/")
        .client(okHttpClient)
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
            val response = api.shortenUrl(request)
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
            val response = api.getLinks()
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
