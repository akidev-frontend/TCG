package com.tcg.portfolio.data.model

data class CardInfo(
    val id: Int,
    val setId: Int,
    val name: String,
    val number: String,
    val language: String,
    val rarity: String?,
    val variant: String?
)