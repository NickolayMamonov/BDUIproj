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

private const val TAG = "ManualDeserializer"

/**
 * Специальный десериализатор для корневых компонентов, которые имеют вложенный объект "component"
 */
class ManualComponentDeserializer : JsonDeserializer<UIComponentWrapper> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): UIComponentWrapper {
        if (json == null || !json.isJsonObject) {
            Log.e(TAG, "JSON is null or not an object")
            return UIComponentWrapper(id = "_fallback_root", type = "unknown")
        }

        val jsonObject = json.asJsonObject
        Log.d(TAG, "Deserializing root component: $jsonObject")

        // Извлекаем тип компонента
        val type = if (jsonObject.has("type") && !jsonObject.get("type").isJsonNull) {
            jsonObject.get("type").asString
        } else {
            "unknown"
        }
        
        Log.d(TAG, "Root component type: $type")

        // Проверяем наличие вложенного объекта "component"
        if (jsonObject.has("component") && !jsonObject.get("component").isJsonNull && jsonObject.get("component").isJsonObject) {
            val componentObject = jsonObject.getAsJsonObject("component")
            Log.d(TAG, "Found nested component object: $componentObject")
            
            // Извлекаем id из вложенного объекта
            val id = if (componentObject.has("id") && !componentObject.get("id").isJsonNull) {
                componentObject.get("id").asString
            } else {
                "_fallback_${System.currentTimeMillis()}"
            }
            
            Log.d(TAG, "Component id: $id")

            // Для списка обрабатываем items
            if (type == "list") {
                if (componentObject.has("items") && !componentObject.get("items").isJsonNull && componentObject.get("items").isJsonArray) {
                    val itemsArray = componentObject.getAsJsonArray("items")
                    Log.d(TAG, "Found items array with ${itemsArray.size()} elements")
                    
                    val items = mutableListOf<UIComponentWrapper>()
                    
                    // Десериализуем каждый элемент списка
                    for (i in 0 until itemsArray.size()) {
                        val itemElement = itemsArray.get(i)
                        if (itemElement.isJsonObject) {
                            val item = context?.deserialize<UIComponentWrapper>(itemElement, UIComponentWrapper::class.java)
                            if (item != null) {
                                items.add(item)
                                Log.d(TAG, "Added item: ${item.id}, type: ${item.type}")
                            }
                        }
                    }
                    
                    // Извлекаем остальные свойства
                    val padding = extractPadding(componentObject)
                    val margin = extractMargin(componentObject)
                    val dividerEnabled = getSafeBoolean(componentObject, "divider", false)
                    val dividerColor = getSafeString(componentObject, "dividerColor")
                    val orientation = getSafeString(componentObject, "orientation") ?: "vertical"
                    
                    // Создаем компонент списка
                    return UIComponentWrapper(
                        id = id,
                        type = type,
                        componentType = type,
                        items = items,
                        padding = padding,
                        margin = margin,
                        dividerEnabled = dividerEnabled,
                        dividerColor = dividerColor,
                        orientation = orientation
                    )
                } else {
                    Log.w(TAG, "List component without items array")
                }
            }
            // Для контейнера обрабатываем children
            else if (type == "container") {
                if (componentObject.has("children") && !componentObject.get("children").isJsonNull && componentObject.get("children").isJsonArray) {
                    val childrenArray = componentObject.getAsJsonArray("children")
                    Log.d(TAG, "Found children array with ${childrenArray.size()} elements")
                    
                    val children = mutableListOf<UIComponentWrapper>()
                    
                    // Десериализуем каждый дочерний элемент
                    for (i in 0 until childrenArray.size()) {
                        val childElement = childrenArray.get(i)
                        if (childElement.isJsonObject) {
                            val child = context?.deserialize<UIComponentWrapper>(childElement, UIComponentWrapper::class.java)
                            if (child != null) {
                                children.add(child)
                                Log.d(TAG, "Added child: ${child.id}, type: ${child.type}")
                            }
                        }
                    }
                    
                    // Извлекаем остальные свойства
                    val padding = extractPadding(componentObject)
                    val margin = extractMargin(componentObject)
                    val background = getSafeString(componentObject, "backgroundColor")
                    val orientation = getSafeString(componentObject, "orientation") ?: "vertical"
                    val action = extractAction(componentObject)
                    
                    // Создаем компонент контейнера
                    return UIComponentWrapper(
                        id = id,
                        type = type,
                        componentType = type,
                        components = children,
                        padding = padding,
                        margin = margin,
                        background = background,
                        orientation = orientation,
                        action = action
                    )
                } else {
                    Log.w(TAG, "Container component without children array")
                }
            }
        }
        
        // Если не смогли обработать специальным образом, возвращаем базовую обертку
        Log.w(TAG, "Falling back to default deserialization")
        return UIComponentWrapper(
            id = "_fallback_${System.currentTimeMillis()}",
            type = type,
            componentType = type
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
            Log.e(TAG, "Error getting string value for key: $key", e)
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
            Log.e(TAG, "Error getting int value for key: $key", e)
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
            Log.e(TAG, "Error getting boolean value for key: $key", e)
            defaultValue
        }
    }

    private fun extractPadding(jsonObject: JsonObject): Padding? {
        return try {
            if (jsonObject.has("padding") && !jsonObject.get("padding").isJsonNull && jsonObject.get("padding").isJsonObject) {
                val paddingObj = jsonObject.getAsJsonObject("padding")
                Padding(
                    left = getSafeInt(paddingObj, "left") ?: getSafeInt(paddingObj, "start") ?: 0,
                    top = getSafeInt(paddingObj, "top") ?: 0,
                    right = getSafeInt(paddingObj, "right") ?: getSafeInt(paddingObj, "end") ?: 0,
                    bottom = getSafeInt(paddingObj, "bottom") ?: 0
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting padding", e)
            null
        }
    }

    private fun extractMargin(jsonObject: JsonObject): Margin? {
        return try {
            if (jsonObject.has("margin") && !jsonObject.get("margin").isJsonNull && jsonObject.get("margin").isJsonObject) {
                val marginObj = jsonObject.getAsJsonObject("margin")
                Margin(
                    left = getSafeInt(marginObj, "left") ?: getSafeInt(marginObj, "start") ?: 0,
                    top = getSafeInt(marginObj, "top") ?: 0,
                    right = getSafeInt(marginObj, "right") ?: getSafeInt(marginObj, "end") ?: 0,
                    bottom = getSafeInt(marginObj, "bottom") ?: 0
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error extracting margin", e)
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
            Log.e(TAG, "Error extracting action", e)
            null
        }
    }
}