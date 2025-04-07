package dev.whysoezzy.bduiproj.api

import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.whysoezzy.bduiproj.model.*
import dev.whysoezzy.bduiproj.utils.UIComponentWrapper

/**
 * Класс для маппинга серверных компонентов в клиентские
 */
object ServerToClientMapper {
    private const val TAG = "ServerToClientMapper"

    /**
     * Преобразует серверную обертку компонента в клиентскую
     */
    fun mapComponentWrapper(serverWrapper: UIComponentWrapper): UIComponent? {
        try {
            return when (serverWrapper.type) {
                "container" -> mapContainer(serverWrapper)
                "text" -> mapText(serverWrapper)
                "button" -> mapButton(serverWrapper)
                "image" -> mapImage(serverWrapper)
                "input" -> mapInput(serverWrapper)
                "list" -> mapList(serverWrapper)
                else -> {
                    Log.e(TAG, "Unknown component type: ${serverWrapper.type}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error mapping component: ${e.message}", e)
            return null
        }
    }

    /**
     * Преобразует серверный контейнер в клиентский
     */
    private fun mapContainer(wrapper: UIComponentWrapper): ContainerComponent {
        // Обработка дочерних компонентов
        val children = wrapper.components?.mapNotNull { childWrapper ->
            // Преобразуем компонент и затем оборачиваем его в UIComponentWrapper
            val component = mapComponentWrapper(childWrapper)
            component?.let { UIComponentWrapper(it.id, it.type) }
        } ?: emptyList()

        return ContainerComponent(
            id = wrapper.id ?: "_fallback_container",
            type = "container",
            components = children,
            orientation = wrapper.orientation ?: "vertical",
            padding = wrapper.padding,
            margin = wrapper.margin,
            background = wrapper.background,
            action = wrapper.action
        )
    }

    /**
     * Преобразует серверный текст в клиентский
     */
    private fun mapText(wrapper: UIComponentWrapper): TextComponent {
        return TextComponent(
            id = wrapper.id ?: "_fallback_text",
            type = "text",
            text = wrapper.text ?: "",
            textSize = wrapper.textSize,
            textColor = wrapper.textColor,
            fontWeight = wrapper.fontWeight,
            textAlign = wrapper.textAlign,
            padding = wrapper.padding,
            margin = wrapper.margin
        )
    }

    /**
     * Преобразует серверную кнопку в клиентскую
     */
    private fun mapButton(wrapper: UIComponentWrapper): ButtonComponent {
        return ButtonComponent(
            id = wrapper.id ?: "_fallback_button",
            type = "button",
            text = wrapper.text ?: "",
            action = wrapper.action ?: Action("none", null, null),
            textColor = wrapper.textColor,
            backgroundColor = wrapper.backgroundColor,
            cornerRadius = wrapper.cornerRadius,
            padding = wrapper.padding,
            margin = wrapper.margin,
            enabled = wrapper.enabled
        )
    }

    /**
     * Преобразует серверное изображение в клиентское
     */
    private fun mapImage(wrapper: UIComponentWrapper): ImageComponent {
        return ImageComponent(
            id = wrapper.id ?: "_fallback_image",
            type = "image",
            url = wrapper.url ?: "",
            contentScale = wrapper.contentScale,
            width = wrapper.width,
            height = wrapper.height,
            cornerRadius = wrapper.cornerRadius,
            padding = wrapper.padding,
            margin = wrapper.margin
        )
    }

    /**
     * Преобразует серверный ввод в клиентский
     */
    private fun mapInput(wrapper: UIComponentWrapper): InputComponent {
        return InputComponent(
            id = wrapper.id ?: "_fallback_input",
            type = "input",
            hint = wrapper.hint,
            initialValue = wrapper.initialValue,
            inputType = wrapper.inputType ?: "text",
            maxLength = wrapper.maxLength,
            backgroundColor = wrapper.backgroundColor,
            cornerRadius = wrapper.cornerRadius,
            padding = wrapper.padding,
            margin = wrapper.margin
        )
    }

    /**
     * Преобразует серверный список в клиентский
     */
    private fun mapList(wrapper: UIComponentWrapper): ListComponent {
        // Обработка элементов списка
        val items = wrapper.items?.mapNotNull { itemWrapper ->
            // Преобразуем компонент и затем оборачиваем его снова в UIComponentWrapper
            val component = mapComponentWrapper(itemWrapper)
            component?.let { UIComponentWrapper(it.id, it.type) }
        } ?: emptyList()

        return ListComponent(
            id = wrapper.id ?: "_fallback_list",
            type = "list",
            items = items,
            orientation = wrapper.orientation ?: "vertical",
            padding = wrapper.padding,
            margin = wrapper.margin,
            dividerEnabled = wrapper.dividerEnabled,
            dividerColor = wrapper.dividerColor
        )
    }
}