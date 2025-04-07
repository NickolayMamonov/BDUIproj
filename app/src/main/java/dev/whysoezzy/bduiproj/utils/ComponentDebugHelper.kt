package dev.whysoezzy.bduiproj.utils

import android.util.Log
import dev.whysoezzy.bduiproj.model.Screen

object ComponentDebugHelper {
    private const val TAG = "ComponentDebugHelper"

    /**
     * Проверяет корневой компонент и его детей
     * @return true если структура компонента валидна, false иначе
     */
    fun validateScreen(screen: Screen?): Boolean {
        if (screen == null) {
            Log.e(TAG, "Screen is null")
            return false
        }

        if (screen.rootComponent == null) {
            Log.e(TAG, "Root component is null in screen: ${screen.id}")
            return false
        }

        // Проверяем, что у корневого компонента установлен правильный тип
        val rootComponent = screen.rootComponent
        val rootType = getEffectiveType(rootComponent)

        if (rootType != "container") {
            Log.w(TAG, "Root component should be a container, got: $rootType")
            // Исправляем тип корневого компонента принудительно
            fixRootComponent(rootComponent)
        }

        Log.d(TAG, "Screen structure validation started")
        Log.d(TAG, "Screen: id=${screen.id}, title=${screen.title}")
        Log.d(
            TAG,
            "Root component: id=${rootComponent.id}, effective type=${getEffectiveType(rootComponent)}"
        )

        val rootValid = validateComponentWrapper(rootComponent, 0)
        Log.d(TAG, "Screen structure validation ${if (rootValid) "passed" else "failed"}")
        return rootValid
    }

    /**
     * Исправляет тип корневого компонента, если он неверный
     */
    private fun fixRootComponent(component: UIComponentWrapper) {
        if (component.id == "main_container") {
            Log.d(TAG, "Fixing root component type to 'container'")
            // Устанавливаем тип принудительно с помощью reflection
            try {
                val typeField = UIComponentWrapper::class.java.getDeclaredField("type")
                typeField.isAccessible = true
                typeField.set(component, "container")

                val componentTypeField =
                    UIComponentWrapper::class.java.getDeclaredField("componentType")
                componentTypeField.isAccessible = true
                componentTypeField.set(component, "container")

                Log.d(TAG, "Root component fixed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fix root component: ${e.message}")
            }
        }
    }

    /**
     * Рекурсивно проверяет компонент и его детей
     */
    private fun validateComponentWrapper(component: UIComponentWrapper?, level: Int): Boolean {
        if (component == null) {
            Log.e(TAG, "${getIndent(level)}Component is null")
            return false
        }

        val indent = getIndent(level)
        val effectiveType = getEffectiveType(component)
        Log.d(TAG, "$indent Validating component: id=${component.id}, type=$effectiveType")

        // Проверяем обязательные поля в зависимости от типа компонента
        val isValid = when (effectiveType) {
            "container" -> {
                // Контейнер должен иметь components массив
                if (component.components == null) {
                    Log.w(TAG, "$indent Container components array is null: ${component.id}")
                    // Для контейнера отсутствие компонентов не является ошибкой
                    true
                } else {
                    // Проверяем каждый дочерний компонент
                    var allValid = true
                    component.components.forEachIndexed { index, child ->
                        if (!validateComponentWrapper(child, level + 1)) {
                            Log.e(
                                TAG,
                                "$indent Child $index is invalid in container: ${component.id}"
                            )
                            allValid = false
                        }
                    }
                    allValid
                }
            }

            "text" -> {
                // Текстовый компонент должен иметь text
                if (component.text == null) {
                    Log.e(TAG, "$indent Text component has no text: ${component.id}")
                    false
                } else {
                    true
                }
            }

            "button" -> {
                // Кнопка должна иметь text и action
                if (component.text == null) {
                    Log.e(TAG, "$indent Button has no text: ${component.id}")
                    false
                } else if (component.action == null) {
                    Log.e(TAG, "$indent Button has no action: ${component.id}")
                    false
                } else {
                    true
                }
            }

            "image" -> {
                // Изображение должно иметь URL
                if (component.url == null) {
                    Log.e(TAG, "$indent Image has no URL: ${component.id}")
                    false
                } else {
                    true
                }
            }

            "list" -> {
                // Список должен иметь массив items
                if (component.items == null) {
                    Log.e(TAG, "$indent List items array is null: ${component.id}")
                    false
                } else {
                    // Проверяем каждый элемент списка
                    var allValid = true
                    component.items.forEachIndexed { index, item ->
                        if (!validateComponentWrapper(item, level + 1)) {
                            Log.e(TAG, "$indent Item $index is invalid in list: ${component.id}")
                            allValid = false
                        }
                    }
                    allValid
                }
            }

            "unknown" -> {
                Log.e(TAG, "$indent Component has unknown type: ${component.id}")
                false
            }

            else -> {
                Log.w(TAG, "$indent Unknown component type: $effectiveType for ${component.id}")
                // Неизвестные типы всё ещё считаются валидными на случай кастомных компонентов
                true
            }
        }

        Log.d(
            TAG,
            "$indent Component ${component.id} validation ${if (isValid) "passed" else "failed"}"
        )
        return isValid
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