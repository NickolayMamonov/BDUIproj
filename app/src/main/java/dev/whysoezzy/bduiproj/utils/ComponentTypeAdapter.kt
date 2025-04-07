package dev.whysoezzy.bduiproj.utils

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

/**
 * Адаптер типов для корректного парсинга компонентов
 */
class ComponentTypeAdapter<T> : JsonDeserializer<T> {
    private val TAG = "ComponentTypeAdapter"

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): T {
        try {
            Log.d(TAG, "Deserializing JSON element for type $typeOfT: ${json.toString().take(100)}...")
            
            // Для отладки
            if (json.isJsonObject) {
                val jsonObject = json.asJsonObject
                if (jsonObject.has("type")) {
                    Log.d(TAG, "Component type: ${jsonObject.get("type").asString}")
                }
                
                if (jsonObject.has("component")) {
                    Log.d(TAG, "Component has nested component field")
                }
                
                // Для ListComponent проверяем items
                if (jsonObject.has("items")) {
                    val items = jsonObject.getAsJsonArray("items")
                    Log.d(TAG, "List component has ${items.size()} items")
                    
                    if (items.size() > 0) {
                        val firstItem = items.get(0).asJsonObject
                        if (firstItem.has("type")) {
                            Log.d(TAG, "First item type: ${firstItem.get("type").asString}")
                        }
                    }
                }
                
                // Для ContainerComponent проверяем children
                if (jsonObject.has("children")) {
                    val children = jsonObject.getAsJsonArray("children")
                    Log.d(TAG, "Container has ${children.size()} children")
                    
                    if (children.size() > 0) {
                        val firstChild = children.get(0).asJsonObject
                        if (firstChild.has("type")) {
                            Log.d(TAG, "First child type: ${firstChild.get("type").asString}")
                        }
                    }
                }
            }
            
            // Используем стандартную десериализацию для типа
            return context.deserialize(json, typeOfT)
        } catch (e: Exception) {
            Log.e(TAG, "Error deserializing component: ${e.message}", e)
            throw JsonParseException("Error deserializing component", e)
        }
    }
}