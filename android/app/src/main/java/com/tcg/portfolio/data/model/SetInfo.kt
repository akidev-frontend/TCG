package com.tcg.portfolio.data.model

data class SetInfo(
    val id: Int,
    val name: String,
    val code: String,
    val language: String,
    val series: String?,
    val totalCards: Int
)