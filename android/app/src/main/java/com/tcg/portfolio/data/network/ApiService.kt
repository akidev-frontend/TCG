package com.tcg.portfolio.data.network

import com.tcg.portfolio.data.model.ScanResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Part

interface ApiService {

    @GET("health")
    suspend fun healthCheck(): Map<String, String>

    @Multipart
    @POST("scan")
    suspend fun scanCard(@Part image: MultipartBody.Part): ScanResponse

    @GET("sets")
    suspend fun getSets(): List<SetInfo>

    @GET("sets/{id}")
    suspend fun getSet(@Path("id") id: Int): SetInfo

    @GET("sets/{id}/progress")
    suspend fun getSetProgress(@Path("id") id: Int): SetProgress

    @GET("cards")
    suspend fun getCards(
        @retrofit2.http.Query("set_id") setId: Int? = null,
        @retrofit2.http.Query("name") name: String? = null
    ): List<CardInfo>

    @GET("cards/{id}")
    suspend fun getCard(@Path("id") id: Int): CardInfo

    @GET("collection")
    suspend fun getCollection(): List<CollectionEntry>

    @POST("collection")
    suspend fun addToCollection(@Body entry: Map<String, Any>): CollectionEntry

    @PUT("collection/{id}")
    suspend fun updateCollection(
        @Path("id") id: Int,
        @Body entry: Map<String, Any>
    ): CollectionEntry

    @DELETE("collection/{id}")
    suspend fun deleteFromCollection(@Path("id") id: Unit)
}