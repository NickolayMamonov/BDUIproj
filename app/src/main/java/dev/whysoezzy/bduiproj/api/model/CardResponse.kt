package dev.whysoezzy.bduiproj.api.model

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа для карточки с сервера
 */
data class CardResponse(
    val title: String,
    val type: String, // "HEADER" или "ROW"
    val image: Int
)