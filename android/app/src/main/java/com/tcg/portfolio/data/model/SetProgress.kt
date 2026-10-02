package com.tcg.portfolio.data.model

data class SetProgress(
    val setId: Int,
    val setName: String,
    val setCode: String,
    val totalCards: Int,
    val ownedCards: Int,
    val missingCards: Int,
    val progressPercentage: Float
)