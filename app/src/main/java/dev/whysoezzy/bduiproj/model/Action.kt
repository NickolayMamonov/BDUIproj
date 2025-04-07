package dev.whysoezzy.bduiproj.model

// Класс для действий
data class Action(
    val type: String,
    val url: String?,
    val payload: Map<String, String>?
)