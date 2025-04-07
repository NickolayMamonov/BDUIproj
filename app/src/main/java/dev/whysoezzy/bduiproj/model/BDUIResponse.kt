package dev.whysoezzy.bduiproj.model

import com.google.gson.annotations.SerializedName
import dev.whysoezzy.bduiproj.utils.UIComponentWrapper

/**
 * Класс для получения ответа с сервера о UI экрана
 */
data class BDUIResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String? = null,
    
    @SerializedName("rootComponent")
    val rootComponent: UIComponentWrapper? = null,
    
    @SerializedName("toolbarColor")
    val toolbarColor: String? = null,
    
    @SerializedName("backgroundColor")
    val backgroundColor: String? = null,
    
    @SerializedName("statusBarColor")
    val statusBarColor: String? = null
) {
    // Для отладки
    override fun toString(): String {
        return "BDUIResponse(id='$id', title=$title, rootComponent=${rootComponent?.id}, toolbarColor=$toolbarColor, backgroundColor=$backgroundColor, statusBarColor=$statusBarColor)"
    }
}

/**
 * Функция-расширение для преобразования BDUIResponse в Screen
 */
fun BDUIResponse.toScreen(): Screen {
    return Screen(
        id = this.id,
        title = this.title,
        rootComponent = this.rootComponent,
        toolbarColor = this.toolbarColor,
        backgroundColor = this.backgroundColor,
        statusBarColor = this.statusBarColor
    )
}
