package dev.whysoezzy.bduiproj.model

import com.google.gson.annotations.SerializedName

/**
 * Упрощенная модель экрана
 */
data class SimpleScreenResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("items")
    val items: List<SimpleCardResponse>,
    
    @SerializedName("backgroundColor")
    val backgroundColor: String = "#F5F5F5",
    
    @SerializedName("toolbarColor")
    val toolbarColor: String = "#2196F3"
) {
    // Для отладки
    override fun toString(): String {
        return "SimpleScreenResponse(id='$id', title='$title', items=${items.size}, backgroundColor='$backgroundColor', toolbarColor='$toolbarColor')"
    }
    
    /**
     * Преобразует упрощенную модель в стандартную модель экрана для BDUI
     */
    fun toScreen(): Screen {
        return Screen(
            id = this.id,
            title = this.title,
            rootComponent = null, // Не используем rootComponent, вместо этого переопределим отображение в UI
            backgroundColor = this.backgroundColor,
            toolbarColor = this.toolbarColor
        )
    }
}

/**
 * Упрощенная модель карточки
 */
data class SimpleCardResponse(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("type")
    val type: String,  // "HEADER", "ROW", "DETAIL", "BUTTON"
    
    @SerializedName("image")
    val image: String,
    
    @SerializedName("actionUrl")
    val actionUrl: String? = null
) {
    // Для отладки
    override fun toString(): String {
        return "SimpleCardResponse(id='$id', title='$title', type='$type', image=$image, actionUrl=$actionUrl)"
    }
}