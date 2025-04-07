package dev.whysoezzy.bduiproj.ui.renders

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.whysoezzy.bduiproj.ui.utils.parseColor
import dev.whysoezzy.bduiproj.ui.utils.toModifierPadding
import dev.whysoezzy.bduiproj.ui.utils.toPaddingValues
import dev.whysoezzy.bduiproj.utils.UIComponentWrapper


private const val TAG = "RenderContainer"

@Composable
fun RenderContainer(
    component: UIComponentWrapper?,
    onNavigate: (String) -> Unit,
    onApiCall: (String, Map<String, String>?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (component == null) {
        Log.e(TAG, "Container component is null")
        ErrorComponent("Container component is null", modifier)
        return
    }

    // Получаем все необходимые свойства с проверкой на null
    val isVertical = component.orientation != "horizontal"
    val backgroundColor = component.background?.let { parseColor(it) }
    val padding = component.padding?.toPaddingValues() ?: PaddingValues(0.dp)
    val components = component.components

    // Логируем детали контейнера для отладки
    Log.d(TAG, "Container: id=${component.id}, orientation=${if (isVertical) "vertical" else "horizontal"}")
    Log.d(TAG, "Container background: ${component.background}, padding: ${component.padding}, margin: ${component.margin}")
    Log.d(TAG, "Container components count: ${components?.size ?: 0}")

    val containerModifier = modifier
        .padding(component.margin.toModifierPadding())
        .let { mod ->
            // Добавляем обработку нажатия, если есть действие
            component.action?.let { action ->
                when (action.type) {
                    "navigate" -> {
                        action.url?.let { url ->
                            val interactionSource = remember { MutableInteractionSource() }
                            mod.clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                Log.d(TAG, "Container clicked, navigating to: $url")
                                onNavigate(url)
                            }
                        } ?: mod
                    }
                    "api" -> {
                        action.url?.let { url ->
                            val interactionSource = remember { MutableInteractionSource() }
                            mod.clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                Log.d(TAG, "Container clicked, calling API: $url")
                                onApiCall(url, action.payload)
                            }
                        } ?: mod
                    }
                    else -> mod
                }
            } ?: mod
        }
        .let { mod ->
            backgroundColor?.let { mod.background(it) } ?: mod
        }
        .padding(padding)

    // Правильно обрабатываем отсутствующий или пустой массив компонентов
    if (components.isNullOrEmpty()) {
        Log.w(TAG, "Container has no components: ${component.id}")

        // Просто отображаем пустой стилизованный контейнер
        if (isVertical) {
            Column(
                modifier = containerModifier
            ) {
                // Пустой контейнер
            }
        } else {
            Row(
                modifier = containerModifier
            ) {
                // Пустой контейнер
            }
        }
        return
    }

    // Рендерим детей в зависимости от ориентации
    if (isVertical) {
        Column(
            modifier = containerModifier
        ) {
            components.forEachIndexed { index, childComponent ->
                if (childComponent != null) {
                    Log.d(TAG, "Rendering child $index in container ${component.id}: ${childComponent.id}, type=${childComponent.type ?: childComponent.componentType}")
                    RenderComponent(
                        component = childComponent,
                        onNavigate = onNavigate,
                        onApiCall = onApiCall,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Log.e(TAG, "Child component $index is null in container: ${component.id}")
                    // Показываем заглушку для null дочернего элемента
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray.copy(alpha = 0.3f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Missing Component",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    } else {
        Row(
            modifier = containerModifier
        ) {
            components.forEachIndexed { index, childComponent ->
                if (childComponent != null) {
                    Log.d(TAG, "Rendering child $index in container ${component.id}: ${childComponent.id}, type=${childComponent.type ?: childComponent.componentType}")
                    RenderComponent(
                        component = childComponent,
                        onNavigate = onNavigate,
                        onApiCall = onApiCall,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Log.e(TAG, "Child component $index is null in container: ${component.id}")
                    // Показываем заглушку для null дочернего элемента
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .background(Color.LightGray.copy(alpha = 0.3f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Missing Component",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}