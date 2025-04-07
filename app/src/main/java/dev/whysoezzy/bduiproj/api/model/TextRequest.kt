package dev.whysoezzy.bduiproj.api.model

import com.google.gson.annotations.SerializedName

/**
 * Модель для отправки текстового запроса
 */
data class TextRequest(
    val text: String
)