package dev.whysoezzy.bduiproj.utils

import android.util.Log
import com.google.gson.annotations.SerializedName
import dev.whysoezzy.bduiproj.model.Action
import dev.whysoezzy.bduiproj.model.ButtonComponent
import dev.whysoezzy.bduiproj.model.ContainerComponent
import dev.whysoezzy.bduiproj.model.ImageComponent
import dev.whysoezzy.bduiproj.model.InputComponent
import dev.whysoezzy.bduiproj.model.ListComponent
import dev.whysoezzy.bduiproj.model.Margin
import dev.whysoezzy.bduiproj.model.Padding
import dev.whysoezzy.bduiproj.model.TextComponent
import dev.whysoezzy.bduiproj.model.UIComponent

private const val TAG = "UIComponentWrapper"

// Wrapper класс для десериализации полиморфных компонентов
data class UIComponentWrapper(
    // Базовые свойства компонента
    val id: String? = null,
    val type: String? = null,

    // Поле для дискриминатора
    @SerializedName("componentType")
    val componentType: String? = null,

    // Свойства контейнера
    val orientation: String? = null,
    @SerializedName(value = "components", alternate = ["children"])
    val components: List<UIComponentWrapper>? = null,
    val background: String? = null,

    // Свойства текста
    val text: String? = null,
    val textSize: Int? = null,
    val textColor: String? = null,
    val fontWeight: String? = null,
    val textAlign: String? = null,

    // Свойства кнопки
    val action: Action? = null,
    val backgroundColor: String? = null,
    val cornerRadius: Int? = null,
    val enabled: Boolean = true,

    // Свойства изображения
    val url: String? = null,
    val contentScale: String? = null,
    val width: Int? = null,
    val height: Int? = null,

    // Свойства ввода
    val hint: String? = null,
    val initialValue: String? = null,
    val inputType: String? = null,
    val maxLength: Int? = null,

    // Свойства списка
    val items: List<UIComponentWrapper>? = null,
    val dividerEnabled: Boolean = false,
    val dividerColor: String? = null,

    // Общие свойства
    val padding: Padding? = null,
    val margin: Margin? = null
) {
    // Метод для получения эффективного типа компонента
    fun getEffectiveType(): String {
        // В первую очередь используем явно заданный тип
        if (!componentType.isNullOrEmpty()) {
            return componentType
        }

        if (!type.isNullOrEmpty()) {
            return type
        }

        // Специальная обработка для корневого контейнера
        if (id == "main_container" || id == "root_container") {
            return "container"
        }

        // Эвристики для определения типа по ID
        id?.let {
            when {
                it.contains("container") -> return "container"
                it.contains("text") -> return "text"
                it.contains("button") -> return "button"
                it.contains("image") -> return "image"
                it.contains("input") -> return "input"
                it.contains("list") -> return "list"
                else -> {
                    Log.w(TAG, "Cannot determine component type for id=$it, using unknown")
                    return "unknown"
                }
            }
        }

        // Эвристики по содержимому
        when {
            components != null -> return "container"
            text != null && action == null -> return "text"
            text != null && action != null -> return "button"
            url != null -> return "image"
            hint != null || inputType != null -> return "input"
            items != null -> return "list"
        }

        return "unknown"
    }

    // Преобразование wrapper в конкретный компонент
    fun toComponent(): UIComponent? {
        try {
            // Проверяем, что необходимые поля не null
            val safeId = id ?: run {
                Log.e(TAG, "Component has no id, using fallback")
                "_fallback_${System.currentTimeMillis()}"
            }

            // Определяем действительный тип компонента
            val actualType = getEffectiveType()

            Log.d(TAG, "Converting component $safeId with determined type $actualType")

            return when (actualType) {
                "container" -> ContainerComponent(
                    id = safeId,
                    type = type ?: "container",
                    orientation = orientation ?: "vertical",
                    components = components ?: emptyList(),
                    padding = padding,
                    margin = margin,
                    background = background
                )
                "text" -> TextComponent(
                    id = safeId,
                    type = type ?: "text",
                    text = text ?: "",
                    textSize = textSize,
                    textColor = textColor,
                    fontWeight = fontWeight,
                    textAlign = textAlign,
                    padding = padding,
                    margin = margin
                )
                "button" -> ButtonComponent(
                    id = safeId,
                    type = type ?: "button",
                    text = text ?: "",
                    action = action ?: Action("none", null, null),
                    textColor = textColor,
                    backgroundColor = backgroundColor,
                    cornerRadius = cornerRadius,
                    padding = padding,
                    margin = margin,
                    enabled = enabled
                )
                "image" -> ImageComponent(
                    id = safeId,
                    type = type ?: "image",
                    url = url ?: "",
                    contentScale = contentScale,
                    width = width,
                    height = height,
                    cornerRadius = cornerRadius,
                    padding = padding,
                    margin = margin
                )
                "input" -> InputComponent(
                    id = safeId,
                    type = type ?: "input",
                    hint = hint,
                    initialValue = initialValue,
                    inputType = inputType ?: "text",
                    maxLength = maxLength,
                    backgroundColor = backgroundColor,
                    cornerRadius = cornerRadius,
                    padding = padding,
                    margin = margin
                )
                "list" -> ListComponent(
                    id = safeId,
                    type = type ?: "list",
                    items = items ?: emptyList(),
                    orientation = orientation ?: "vertical",
                    padding = padding,
                    margin = margin,
                    dividerEnabled = dividerEnabled,
                    dividerColor = dividerColor
                )
                else -> {
                    Log.w(TAG, "Unknown component type: $actualType for ${safeId}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error converting component: ${e.message}", e)
            return null
        }
    }
}