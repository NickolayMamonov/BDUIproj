package dev.whysoezzy.bduiproj.ui.renders

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import dev.whysoezzy.bduiproj.ui.utils.padding
import dev.whysoezzy.bduiproj.ui.utils.parseColor
import dev.whysoezzy.bduiproj.ui.utils.toModifierPadding
import dev.whysoezzy.bduiproj.ui.utils.toPaddingValues
import dev.whysoezzy.bduiproj.utils.UIComponentWrapper

@Composable
fun RenderText(
    component: UIComponentWrapper,
    modifier: Modifier = Modifier
) {
    val textColor = component.textColor?.let { parseColor(it) } ?: LocalContentColor.current
    val fontWeight = when (component.fontWeight) {
        "bold" -> FontWeight.Bold
        "medium" -> FontWeight.Medium
        "light" -> FontWeight.Light
        else -> FontWeight.Normal
    }

    val textAlign = when (component.textAlign) {
        "center" -> TextAlign.Center
        "right" -> TextAlign.End
        else -> TextAlign.Start
    }

    Text(
        text = component.text ?: "",
        color = textColor,
        fontSize = component.textSize?.sp ?: 16.sp,
        fontWeight = fontWeight,
        textAlign = textAlign,
        modifier = modifier
            .padding(component.margin.toModifierPadding())
            .padding(component.padding.toPaddingValues())
            .fillMaxWidth()
    )
}