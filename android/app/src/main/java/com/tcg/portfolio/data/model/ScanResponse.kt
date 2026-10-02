package com.tcg.portfolio.data.model

data class ScanResponse(
    val success: Boolean,
    val cardId: Int? = null,
    val name: String,
    val setName: String,
    val setCode: String,
    val cardNumber: String,
    val rarity: String?,
    val variant: String?,
    val message: String?
)