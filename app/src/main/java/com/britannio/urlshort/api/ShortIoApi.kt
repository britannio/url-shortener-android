package com.britannio.urlshort.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Query

interface ShortIoApi {
    @POST("links")
    suspend fun shortenUrl(
        @Body request: ShortenUrlRequest
    ): ShortenUrlResponse

    @GET("api/links")
    suspend fun getLinks(
        @Query("domain_id") domainId: Int = 668301,
        @Query("limit") limit: Int = 30,
        @Query("dateSortOrder") dateSortOrder: String = "desc"
    ): GetLinksResponse
}

data class ShortenUrlRequest(
    val domain: String = "britann.io",
    val originalURL: String,
    val path: String?,
    val domain_id: Int = 668301
)

data class ShortenUrlResponse(
    val originalURL: String,
    val shortURL: String
)

data class GetLinksResponse(
    val links: List<LinkInfo>,
    val nextPageToken: String?,
    val count: Int
)

data class LinkInfo(
    val originalURL: String,
    val shortURL: String,
    val path: String,
    val createdAt: String
)
