package dev.whysoezzy.bduiproj.utils

import android.util.Log
import dev.whysoezzy.bduiproj.model.BDUIResponse

object DebugUtils {
    private const val TAG = "DebugUtils"

    /**
     * Рекурсивно логирует структуру SDUI ответа
     */
//    fun logSduiResponse(response: BDUIResponse?) {
//        if (response == null) {
//            Log.e(TAG, "SDUI Response is null")
//            return
//        }
//
//        Log.d(TAG, "SDUI Response: version=${response.version}, timestamp=${response.timestamp}")
//
//        val screen = response.screen
//        if (screen == null) {
//            Log.e(TAG, "Screen is null in SDUI response")
//            return
//        }
//
//        Log.d(TAG, "Screen: id=${screen.id}, title=${screen.title}")
//        Log.d(TAG, "Screen properties: backgroundColor=${screen.backgroundColor}, toolbarColor=${screen.toolbarColor}")
//
//        val rootComponent = screen.rootComponent
//        if (rootComponent == null) {
//            Log.e(TAG, "Root component is null in screen")
//            return
//        }
//
//        Log.d(TAG, "Starting to log component tree:")
//        Log.d(TAG, "Root component details:")
//        Log.d(TAG, "- id: ${rootComponent.id}")
//        Log.d(TAG, "- type: ${rootComponent.type}")
//        Log.d(TAG, "- componentType: ${rootComponent.componentType}")
//        Log.d(TAG, "- effective type: ${getEffectiveType(rootComponent)}")
//        Log.d(TAG, "- components count: ${rootComponent.components?.size ?: 0}")
//
//        logComponent(rootComponent, 0)
//        Log.d(TAG, "Finished logging component tree")
//    }

    /**
     * Рекурсивно логирует структуру компонента с отступами для наглядности
     */
    private fun logComponent(component: UIComponentWrapper?, level: Int) {
        if (component == null) {
            Log.e(TAG, "${getIndent(level)}Component is null")
            return
        }

        val indent = getIndent(level)
        val effectiveType = getEffectiveType(component)

        Log.d(TAG, "$indent- Component: id=${component.id}, effective type=$effectiveType")
        Log.d(TAG, "$indent  Raw values: type=${component.type}, componentType=${component.componentType}")

        // Логируем ключевые свойства в зависимости от типа
        when (effectiveType) {
            "container" -> {
                val childCount = component.components?.size ?: 0
                Log.d(TAG, "$indent  Container: orientation=${component.orientation}, children=$childCount")
                Log.d(TAG, "$indent  Padding: ${component.padding}, Margin: ${component.margin}")
                Log.d(TAG, "$indent  Background: ${component.background}")

                component.components?.forEachIndexed { index, child ->
                    Log.d(TAG, "$indent  Child $index:")
                    logComponent(child, level + 1)
                } ?: Log.d(TAG, "$indent  No children")
            }
            "text" -> {
                Log.d(TAG, "$indent  Text: '${component.text}', size=${component.textSize}, color=${component.textColor}, weight=${component.fontWeight}")
                Log.d(TAG, "$indent  Padding: ${component.padding}, Margin: ${component.margin}")
            }
            "button" -> {
                val actionType = component.action?.type ?: "none"
                val actionUrl = component.action?.url ?: "none"
                Log.d(TAG, "$indent  Button: text='${component.text}', action=$actionType, url=$actionUrl")
                Log.d(TAG, "$indent  Enabled: ${component.enabled}, backgroundColor=${component.backgroundColor}, textColor=${component.textColor}")
                Log.d(TAG, "$indent  Padding: ${component.padding}, Margin: ${component.margin}")
            }
            "image" -> {
                Log.d(TAG, "$indent  Image: url=${component.url}, width=${component.width}, height=${component.height}, cornerRadius=${component.cornerRadius}")
                Log.d(TAG, "$indent  ContentScale: ${component.contentScale}")
                Log.d(TAG, "$indent  Padding: ${component.padding}, Margin: ${component.margin}")
            }
            "input" -> {
                Log.d(TAG, "$indent  Input: hint='${component.hint}', type=${component.inputType}, initialValue=${component.initialValue}")
                Log.d(TAG, "$indent  MaxLength: ${component.maxLength}, backgroundColor=${component.backgroundColor}, cornerRadius=${component.cornerRadius}")
                Log.d(TAG, "$indent  Padding: ${component.padding}, Margin: ${component.margin}")
            }
            "list" -> {
                val itemCount = component.items?.size ?: 0
                val dividerEnabled = component.dividerEnabled
                Log.d(TAG, "$indent  List: orientation=${component.orientation}, items=$itemCount, dividerEnabled=$dividerEnabled")
                Log.d(TAG, "$indent  Padding: ${component.padding}, Margin: ${component.margin}")

                component.items?.forEachIndexed { index, item ->
                    Log.d(TAG, "$indent  Item $index:")
                    logComponent(item, level + 2)
                } ?: Log.d(TAG, "$indent  No items")
            }
            else -> {
                Log.d(TAG, "$indent  Unknown type: $effectiveType")
                // Дамп всех доступных свойств для неизвестных типов
                Log.d(TAG, "$indent  All properties: id=${component.id}, text=${component.text}, url=${component.url}")
                Log.d(TAG, "$indent  Has components: ${!component.components.isNullOrEmpty()}, Has items: ${!component.items.isNullOrEmpty()}")
            }
        }
    }

    /**
     * Определяет эффективный тип компонента на основе всех доступных данных
     */
    private fun getEffectiveType(component: UIComponentWrapper): String {
        // В первую очередь используем явно заданный тип
        if (!component.componentType.isNullOrEmpty()) {
            return component.componentType
        }

        if (!component.type.isNullOrEmpty()) {
            return component.type
        }

        // Специальная обработка для корневого контейнера
        if (component.id == "main_container") {
            return "container"
        }

        // Эвристики для определения типа по ID
        component.id?.let {
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
            component.components != null -> return "container"
            component.text != null && component.action == null -> return "text"
            component.text != null && component.action != null -> return "button"
            component.url != null -> return "image"
            component.hint != null || component.inputType != null -> return "input"
            component.items != null -> return "list"
        }

        return "unknown"
    }

    private fun getIndent(level: Int): String {
        return "  ".repeat(level)
    }
}