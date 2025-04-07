package dev.whysoezzy.bduiproj.utils

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import dev.whysoezzy.bduiproj.model.Action
import dev.whysoezzy.bduiproj.model.Margin
import dev.whysoezzy.bduiproj.model.Padding
import java.lang.reflect.Type

private const val TAG = "UIComponentDeserializer"

/**
 * Кастомный десериализатор для UIComponentWrapper, который гарантирует, что
 * объект будет создан даже если какие-то поля отсутствуют
 */
class UIComponentWrapperDeserializer : JsonDeserializer<UIComponentWrapper> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): UIComponentWrapper {
        if (json == null || !json.isJsonObject) {
            Log.e(TAG, "JSON is null or not an object")
            return UIComponentWrapper(id = "_fallback_", type = "unknown")
        }

        val jsonObject = json.asJsonObject
        
        // Детальное логирование всего JSON-объекта
        Log.d(TAG, "Deserializing JSON: ${jsonObject}")

        // Проверяем наличие поля components/children
        if (jsonObject.has("children")) {
            Log.d(TAG, "Component has children field: ${jsonObject.get("children")}")
        } else {
            Log.w(TAG, "Component does not have children field!")
        }
        
        if (jsonObject.has("items")) {
            Log.d(TAG, "Component has items field: ${jsonObject.get("items")}")
        } else {
            Log.w(TAG, "Component does not have items field!")
        }

        // Извлекаем данные напрямую
        val id = getSafeString(jsonObject, "id")
        val type = getSafeString(jsonObject, "type")
        val componentType = getSafeString(jsonObject, "componentType")
        val orientation = getSafeString(jsonObject, "orientation")
        val background = getSafeString(jsonObject, "background")
        val text = getSafeString(jsonObject, "text")
        val fontWeight = getSafeString(jsonObject, "fontWeight")
        val textAlign = getSafeString(jsonObject, "textAlign")
        val textColor = getSafeString(jsonObject, "textColor")
        val backgroundColor = getSafeString(jsonObject, "backgroundColor")
        val url = getSafeString(jsonObject, "url")
        val contentScale = getSafeString(jsonObject, "contentScale")
        val hint = getSafeString(jsonObject, "hint")
        val initialValue = getSafeString(jsonObject, "initialValue")
        val inputType = getSafeString(jsonObject, "inputType")
        val dividerColor = getSafeString(jsonObject, "dividerColor")

        val finalId = id ?: "_fallback_${System.currentTimeMillis()}"

        // Определяем тип компонента
        var finalComponentType: String? = null

        // Если это корневой компонент main_container, то явно устанавливаем тип "container"
        if (finalId == "main_container" || finalId == "root_container") {
            finalComponentType = "container"
            Log.d(TAG, "Identified root container with id=$finalId, forcing type=container")
        } else {
            // Для других компонентов используем обычную логику определения типа
            finalComponentType = when {
                componentType != null -> componentType
                type != null -> type
                finalId.contains("container") -> "container"
                finalId.contains("text") -> "text"
                finalId.contains("button") -> "button"
                finalId.contains("image") -> "image"
                finalId.contains("input") -> "input"
                finalId.contains("list") -> "list"
                else -> "unknown"
            }
        }

        Log.d(TAG, "Deserializing component $finalId with type $finalComponentType")

        // Безопасно извлекаем числа
        val textSize = getSafeInt(jsonObject, "textSize")
        val cornerRadius = getSafeInt(jsonObject, "cornerRadius")
        val width = getSafeInt(jsonObject, "width")
        val height = getSafeInt(jsonObject, "height")
        val maxLength = getSafeInt(jsonObject, "maxLength")

        // Безопасно извлекаем булевы значения
        val enabled = getSafeBoolean(jsonObject, "enabled", true)
        val dividerEnabled = getSafeBoolean(jsonObject, "dividerEnabled", false)

        // Извлекаем объекты
        val padding = extractPadding(jsonObject)
        val margin = extractMargin(jsonObject)
        val action = extractAction(jsonObject)

        // Рекурсивно обрабатываем массив компонентов - учитываем оба варианта "children" и "components"
        val components = if (jsonObject.has("children") && !jsonObject.get("children").isJsonNull && jsonObject.get("children").isJsonArray) {
            val componentsArray = jsonObject.getAsJsonArray("children")
            val result = ArrayList<UIComponentWrapper>()

            Log.d(TAG, "Processing children array of size: ${componentsArray.size()}")
            
            for (i in 0 until componentsArray.size()) {
                val element = componentsArray.get(i)
                if (element.isJsonObject) {
                    // Рекурсивно десериализуем каждый дочерний компонент
                    val childComponent = deserialize(element, typeOfT, context)
                    result.add(childComponent)
                    Log.d(TAG, "Added child component: ${childComponent.id}, type=${childComponent.type}")
                }
            }
            result
        } else if (jsonObject.has("components") && !jsonObject.get("components").isJsonNull && jsonObject.get("components").isJsonArray) {
            // Пробуем альтернативное имя поля
            val componentsArray = jsonObject.getAsJsonArray("components")
            val result = ArrayList<UIComponentWrapper>()

            Log.d(TAG, "Processing components array of size: ${componentsArray.size()}")
            
            for (i in 0 until componentsArray.size()) {
                val element = componentsArray.get(i)
                if (element.isJsonObject) {
                    // Рекурсивно десериализуем каждый дочерний компонент
                    val childComponent = deserialize(element, typeOfT, context)
                    result.add(childComponent)
                    Log.d(TAG, "Added child component: ${childComponent.id}, type=${childComponent.type}")
                }
            }
            result
        } else {
            Log.d(TAG, "No children/components array found")
            null
        }

        // Рекурсивно обрабатываем массив элементов списка
        val items = if (jsonObject.has("items") && !jsonObject.get("items").isJsonNull && jsonObject.get("items").isJsonArray) {
            val itemsArray = jsonObject.getAsJsonArray("items")
            val result = ArrayList<UIComponentWrapper>()
            
            Log.d(TAG, "Processing items array of size: ${itemsArray.size()}")

            for (i in 0 until itemsArray.size()) {
                val element = itemsArray.get(i)
                if (element.isJsonObject) {
                    // Рекурсивно десериализуем каждый элемент списка
                    val childItem = deserialize(element, typeOfT, context)
                    result.add(childItem)
                    Log.d(TAG, "Added list item: ${childItem.id}, type=${childItem.type}")
                }
            }
            result
        } else {
            Log.d(TAG, "No items array found")
            null
        }

        // Создаем итоговый объект
        return UIComponentWrapper(
            id = finalId,
            type = finalComponentType,          // Используем финальный тип компонента
            componentType = finalComponentType, // Устанавливаем и как componentType для надежности
            orientation = orientation,
            components = components,
            background = background,
            text = text,
            textSize = textSize,
            textColor = textColor,
            fontWeight = fontWeight,
            textAlign = textAlign,
            action = action,
            backgroundColor = backgroundColor,
            cornerRadius = cornerRadius,
            enabled = enabled,
            url = url,
            contentScale = contentScale,
            width = width,
            height = height,
            hint = hint,
            initialValue = initialValue,
            inputType = inputType,
            maxLength = maxLength,
            items = items,
            dividerEnabled = dividerEnabled,
            dividerColor = dividerColor,
            padding = padding,
            margin = margin
        )
    }

    // Вспомогательные методы
    private fun getSafeString(jsonObject: JsonObject, key: String): String? {
        return try {
            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull) {
                jsonObject.get(key).asString
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getSafeInt(jsonObject: JsonObject, key: String): Int? {
        return try {
            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull && jsonObject.get(key).isJsonPrimitive) {
                val primitive = jsonObject.get(key).asJsonPrimitive
                if (primitive.isNumber) primitive.asInt else null
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getSafeBoolean(jsonObject: JsonObject, key: String, defaultValue: Boolean = false): Boolean {
        return try {
            if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull && jsonObject.get(key).isJsonPrimitive) {
                val primitive = jsonObject.get(key).asJsonPrimitive
                if (primitive.isBoolean) primitive.asBoolean else defaultValue
            } else {
                defaultValue
            }
        } catch (e: Exception) {
            defaultValue
        }
    }

    private fun extractPadding(jsonObject: JsonObject): Padding? {
        return try {
            if (jsonObject.has("padding") && !jsonObject.get("padding").isJsonNull && jsonObject.get("padding").isJsonObject) {
                val paddingObj = jsonObject.getAsJsonObject("padding")
                Padding(
                    left = getSafeInt(paddingObj, "left") ?: 0,
                    top = getSafeInt(paddingObj, "top") ?: 0,
                    right = getSafeInt(paddingObj, "right") ?: 0,
                    bottom = getSafeInt(paddingObj, "bottom") ?: 0
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun extractMargin(jsonObject: JsonObject): Margin? {
        return try {
            if (jsonObject.has("margin") && !jsonObject.get("margin").isJsonNull && jsonObject.get("margin").isJsonObject) {
                val marginObj = jsonObject.getAsJsonObject("margin")
                Margin(
                    left = getSafeInt(marginObj, "left") ?: 0,
                    top = getSafeInt(marginObj, "top") ?: 0,
                    right = getSafeInt(marginObj, "right") ?: 0,
                    bottom = getSafeInt(marginObj, "bottom") ?: 0
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun extractAction(jsonObject: JsonObject): Action? {
        return try {
            if (jsonObject.has("action") && !jsonObject.get("action").isJsonNull && jsonObject.get("action").isJsonObject) {
                val actionObj = jsonObject.getAsJsonObject("action")
                val actionType = getSafeString(actionObj, "type") ?: "none"
                val url = getSafeString(actionObj, "url")

                // Извлекаем payload
                val payload = if (actionObj.has("payload") && !actionObj.get("payload").isJsonNull && actionObj.get("payload").isJsonObject) {
                    val payloadObj = actionObj.getAsJsonObject("payload")
                    val map = mutableMapOf<String, String>()

                    for (entry in payloadObj.entrySet()) {
                        val value = if (entry.value.isJsonPrimitive) {
                            val primitive = entry.value.asJsonPrimitive
                            if (primitive.isString) primitive.asString else primitive.toString()
                        } else {
                            entry.value.toString()
                        }
                        map[entry.key] = value
                    }
                    map
                } else {
                    null
                }

                Action(type = actionType, url = url, payload = payload)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}