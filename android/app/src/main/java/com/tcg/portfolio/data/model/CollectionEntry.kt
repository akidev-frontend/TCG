package com.tcg.portfolio.data.model

data class CollectionEntry(
    val id: Int,
    val cardId: Int,
    val quantity: Int,
    val condition: String?,
    val purchasePrice: Double?,
    val purchaseDate: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
)