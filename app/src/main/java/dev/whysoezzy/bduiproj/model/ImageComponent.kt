package dev.whysoezzy.bduiproj.model

// Компонент изображения
data class ImageComponent(
    override val id: String,
    override val type: String,
    val url: String,
    val contentScale: String?,
    val width: Int?,
    val height: Int?,
    val cornerRadius: Int?,
    val padding: Padding?,
    val margin: Margin?
) : UIComponent