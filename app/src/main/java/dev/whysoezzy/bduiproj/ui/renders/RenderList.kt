package dev.whysoezzy.bduiproj.ui.renders

import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.whysoezzy.bduiproj.ui.utils.padding
import dev.whysoezzy.bduiproj.ui.utils.parseColor
import dev.whysoezzy.bduiproj.ui.utils.toModifierPadding
import dev.whysoezzy.bduiproj.ui.utils.toPaddingValues
import dev.whysoezzy.bduiproj.utils.UIComponentWrapper

private const val TAG = "RenderList"

@Composable
fun RenderList(
    component: UIComponentWrapper,
    onNavigate: (String) -> Unit,
    onApiCall: (String, Map<String, String>?) -> Unit,
    modifier: Modifier = Modifier
) {
    val isVertical = component.orientation != "horizontal"
    val items = component.items ?: emptyList()
    val dividerEnabled = component.dividerEnabled
    val dividerColor = component.dividerColor?.let { parseColor(it) } ?: MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    
    Log.d(TAG, "Rendering list component: id=${component.id}, items count=${items.size}")
    Log.d(TAG, "List orientation: ${if (isVertical) "vertical" else "horizontal"}, divider enabled: $dividerEnabled")
    
    // Дополнительное логирование элементов списка
    items.forEachIndexed { index, item ->
        Log.d(TAG, "Item $index: id=${item.id}, type=${item.type ?: item.componentType}")
    }

    val listModifier = modifier
        .padding(component.margin.toModifierPadding())
        .padding(component.padding.toPaddingValues())

    if (isVertical) {
        LazyColumn(
            modifier = listModifier
        ) {
            items(items) { item ->
                RenderComponent(
                    component = item,
                    onNavigate = onNavigate,
                    onApiCall = onApiCall,
                    modifier = Modifier.fillMaxWidth()
                )

                if (dividerEnabled && item != items.last()) {
                    HorizontalDivider(color = dividerColor)
                }
            }
        }
    } else {
        LazyRow(
            modifier = listModifier
        ) {
            items(items) { item ->
                RenderComponent(
                    component = item,
                    onNavigate = onNavigate,
                    onApiCall = onApiCall,
                    modifier = Modifier.wrapContentWidth()
                )

                if (dividerEnabled && item != items.last()) {
                    HorizontalDivider(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight(),
                        color = dividerColor
                    )
                }
            }
        }
    }
}