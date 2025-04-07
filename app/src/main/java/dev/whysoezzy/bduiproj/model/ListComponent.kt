package dev.whysoezzy.bduiproj.model

import dev.whysoezzy.bduiproj.utils.UIComponentWrapper

// Компонент списка
data class ListComponent(
    override val id: String,
    override val type: String,
    val items: List<UIComponentWrapper>,
    val orientation: String,
    val padding: Padding?,
    val margin: Margin?,
    val dividerEnabled: Boolean,
    val dividerColor: String?
) : UIComponent