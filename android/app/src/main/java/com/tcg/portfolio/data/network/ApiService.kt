package com.tcg.portfolio.data.network

import com.tcg.portfolio.data.model.CardInfo
import com.tcg.portfolio.data.model.CollectionEntry
import com.tcg.portfolio.data.model.ScanResponse
import com.tcg.portfolio.data.model.SetInfo
import com.tcg.portfolio.data.model.SetProgress
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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
        @Query("set_id") setId: Int? = null,
        @Query("name") name: String? = null
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
    suspend fun deleteFromCollection(@Path("id") id: Int)
}