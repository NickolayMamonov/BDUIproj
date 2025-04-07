package dev.whysoezzy.bduiproj.model

data class TextComponent(
    override val id: String,
    override val type: String,
    val text: String,
    val textSize: Int?,
    val textColor: String?,
    val fontWeight: String?,
    val textAlign: String?,
    val padding: Padding?,
    val margin: Margin?
) : UIComponent