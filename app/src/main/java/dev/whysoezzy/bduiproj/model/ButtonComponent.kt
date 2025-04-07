package dev.whysoezzy.bduiproj.model

import dev.whysoezzy.bduiproj.model.Action

// Компонент кнопки
data class ButtonComponent(
    override val id: String,
    override val type: String,
    val text: String,
    val action: Action,
    val textColor: String?,
    val backgroundColor: String?,
    val cornerRadius: Int?,
    val padding: Padding?,
    val margin: Margin?,
    val enabled: Boolean
) : UIComponent