package dev.whysoezzy.bduiproj.util

import android.content.Context
import dev.whysoezzy.bduiproj.R

/**
 * Класс для преобразования строковых идентификаторов изображений в реальные идентификаторы ресурсов Android
 */
object ImageMapper {
    private val imageMap = mapOf(
        // Маппинг ключей сервера на локальные ресурсы
        "header_image" to R.drawable.ic_launcher_foreground,
        "card_image_1" to R.drawable.ic_launcher_foreground,
        "card_image_2" to R.drawable.ic_launcher_foreground,
        "card_image_3" to R.drawable.ic_launcher_foreground,
        "card_image_4" to R.drawable.ic_launcher_foreground,
        "card_image_5" to R.drawable.ic_launcher_foreground
    )
    
    /**
     * Получает идентификатор ресурса на основе строкового идентификатора
     * @param imageKey Строковый идентификатор изображения
     * @return Идентификатор ресурса Android или R.drawable.ic_launcher_foreground по умолчанию
     */
    fun getDrawableResource(imageKey: String): Int {
        return imageMap[imageKey] ?: R.drawable.ic_launcher_foreground
    }
}