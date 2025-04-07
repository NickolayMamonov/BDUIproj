package dev.whysoezzy.bduiproj.model

// Компонент ввода текста
data class InputComponent(
    override val id: String,
    override val type: String,
    val hint: String?,
    val initialValue: String?,
    val inputType: String,
    val maxLength: Int?,
    val backgroundColor: String?,
    val cornerRadius: Int?,
    val padding: Padding?,
    val margin: Margin?
) : UIComponent