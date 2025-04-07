package dev.whysoezzy.bduiproj.ui.renders

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.whysoezzy.bduiproj.ui.utils.padding
import dev.whysoezzy.bduiproj.ui.utils.toModifierPadding
import dev.whysoezzy.bduiproj.ui.utils.toPaddingValues
import dev.whysoezzy.bduiproj.utils.UIComponentWrapper


@Composable
fun RenderImage(
    component: UIComponentWrapper,
    modifier: Modifier = Modifier
) {
    val imageUrl = component.url ?: return
    val width = component.width?.dp
    val height = component.height?.dp
    val cornerRadius = component.cornerRadius ?: 0
    val contentScaleType = when (component.contentScale) {
        "fill" -> ContentScale.FillBounds
        "fit" -> ContentScale.Fit
        "crop" -> ContentScale.Crop
        else -> ContentScale.Inside
    }

    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = contentScaleType,
        modifier = modifier
            .padding(component.margin.toModifierPadding())
            .let { mod ->
                if (width != null && height != null) {
                    mod.size(width, height)
                } else if (width != null) {
                    mod.width(width).wrapContentHeight()
                } else if (height != null) {
                    mod.height(height).fillMaxWidth()
                } else {
                    mod.fillMaxWidth().wrapContentHeight()
                }
            }
            .clip(RoundedCornerShape(cornerRadius.dp))
            .padding(component.padding.toPaddingValues())
    )
}