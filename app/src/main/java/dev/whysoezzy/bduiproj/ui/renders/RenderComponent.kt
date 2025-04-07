package dev.whysoezzy.bduiproj.ui.renders

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.whysoezzy.bduiproj.utils.UIComponentWrapper

private const val TAG = "RenderComponent"

@Composable
fun RenderComponent(
    component: UIComponentWrapper?,
    onNavigate: (String) -> Unit,
    onApiCall: (String, Map<String, String>?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Проверка на null
    if (component == null) {
        Log.e(TAG, "Component is null")
        ErrorComponent("Component is null", modifier)
        return
    }

    // Для отладки
    Log.d(TAG, "Rendering component: id=${component.id}, type=${component.type}, componentType=${component.componentType}")
    Log.d(TAG, "Component details: components=${component.components?.size ?: 0}, items=${component.items?.size ?: 0}")
    component.components?.forEachIndexed { index, child ->
        Log.d(TAG, "Child component $index: id=${child.id}, type=${child.type ?: child.componentType}")
    }

    // Определяем тип компонента с приоритетом уже установленных полей
    val componentType = determineComponentType(component)

    Log.d(TAG, "Component type determined: $componentType")

    // Рендерим компонент в зависимости от его типа
    when (componentType) {
        "container" -> RenderContainer(component, onNavigate, onApiCall, modifier)
        "text" -> RenderText(component, modifier)
        "button" -> RenderButton(component, onNavigate, onApiCall, modifier)
        "image" -> RenderImage(component, modifier)
        "input" -> RenderInput(component, modifier)
        "list" -> RenderList(component, onNavigate, onApiCall, modifier)
        else -> {
            Log.w(TAG, "Unknown or invalid component type: $componentType for id: ${component.id}")
            ErrorComponent("Unknown component type: $componentType", modifier)
        }
    }
}

/**
 * Определяет тип компонента с учетом всех доступных данных
 */
private fun determineComponentType(component: UIComponentWrapper): String {
    // 1. Если явно задан componentType - используем его
    if (!component.componentType.isNullOrEmpty()) {
        return component.componentType
    }

    // 2. Если явно задан type - используем его
    if (!component.type.isNullOrEmpty()) {
        return component.type
    }

    // 3. Специальная обработка main_container
    if (component.id == "main_container") {
        return "container"
    }

    // 4. Пытаемся определить по ID
    component.id?.let { id ->
        when {
            id.contains("container") -> return "container"
            id.contains("text") -> return "text"
            id.contains("button") -> return "button"
            id.contains("image") -> return "image"
            id.contains("input") -> return "input"
            id.contains("list") -> return "list"
            else -> {
                Log.w(TAG, "Cannot determine component type for id=$id, using unknown")
                return "unknown"
            }
        }
    }

    // 5. Анализируем структуру компонента для эвристического определения
    when {
        // Если есть components, вероятно это контейнер
        !component.components.isNullOrEmpty() -> return "container"

        // Если есть text, вероятно это текстовый компонент
        !component.text.isNullOrEmpty() && component.action == null -> return "text"

        // Если есть text и action, вероятно это кнопка
        !component.text.isNullOrEmpty() && component.action != null -> return "button"

        // Если есть url, вероятно это изображение
        !component.url.isNullOrEmpty() -> return "image"

        // Если есть hint или inputType, вероятно это поле ввода
        !component.hint.isNullOrEmpty() || !component.inputType.isNullOrEmpty() -> return "input"

        // Если есть items, вероятно это список
        !component.items.isNullOrEmpty() -> return "list"
    }

    // Если не удалось определить тип
    return "unknown"
}

@Composable
fun ErrorComponent(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(8.dp)
    ) {
        Text(
            text = message,
            color = Color.Red,
            modifier = Modifier.padding(8.dp)
        )
    }
}