package com.tcg.portfolio.data.repository

import com.tcg.portfolio.data.model.CollectionEntry
import com.tcg.portfolio.data.model.ScanResponse
import com.tcg.portfolio.data.model.SetInfo
import com.tcg.portfolio.data.model.SetProgress
import com.tcg.portfolio.data.network.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PortfolioRepository(private val apiService: ApiService) {

    suspend fun healthCheck(): Map<String, String> = apiService.healthCheck()

    suspend fun scanCard(imageBytes: ByteArray): ScanResponse {
        val tempFile = File.createTempFile("scan", ".jpg")
        tempFile.writeBytes(imageBytes)
        val imageBody = tempFile.asRequestBody("image/jpeg".toMediaType())
        val multipart = MultipartBody.Part.createFormData("image", "scan.jpg", imageBody)
        return apiService.scanCard(multipart)
    }

    suspend fun getSets(): List<SetInfo> = apiService.getSets()

    suspend fun getSet(id: Int): SetInfo = apiService.getSet(id)

    suspend fun getSetProgress(id: Int): SetProgress = apiService.getSetProgress(id)

    suspend fun getCards(setId: Int? = null, name: String? = null): List<com.tcg.portfolio.data.model.CardInfo> =
        apiService.getCards(setId = setId, name = name)

    suspend fun getCard(id: Int): com.tcg.portfolio.data.model.CardInfo = apiService.getCard(id)

    suspend fun getCollection(): List<CollectionEntry> = apiService.getCollection()

    suspend fun addToCollection(entry: Map<String, Any>): CollectionEntry = apiService.addToCollection(entry)

    suspend fun updateCollection(id: Int, entry: Map<String, Any>): CollectionEntry = apiService.updateCollection(id, entry)

    suspend fun deleteFromCollection(id: Int) = apiService.deleteFromCollection(id)
}