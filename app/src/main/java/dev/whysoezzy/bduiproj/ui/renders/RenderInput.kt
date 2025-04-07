
package dev.whysoezzy.bduiproj.ui.renders

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.whysoezzy.bduiproj.ui.utils.padding
import dev.whysoezzy.bduiproj.ui.utils.parseColor
import dev.whysoezzy.bduiproj.ui.utils.toModifierPadding
import dev.whysoezzy.bduiproj.ui.utils.toPaddingValues
import dev.whysoezzy.bduiproj.utils.UIComponentWrapper

@Composable
fun RenderInput(
    component: UIComponentWrapper,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(component.initialValue ?: "") }
    val hint = component.hint ?: ""
    val backgroundColor = component.backgroundColor?.let { parseColor(it) } ?: MaterialTheme.colorScheme.surface
    val cornerRadius = component.cornerRadius ?: 4

    val keyboardType = when (component.inputType) {
        "number" -> KeyboardType.Number
        "email" -> KeyboardType.Email
        "password" -> KeyboardType.Password
        else -> KeyboardType.Text
    }

    val visualTransformation = if (component.inputType == "password") {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    OutlinedTextField(
        value = text,
        onValueChange = {
            // Если есть ограничение на длину, применяем его
            if (component.maxLength == null || it.length <= component.maxLength) {
                text = it
            }
        },
        label = { Text(hint) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(cornerRadius.dp),
//        colors = TextFieldDefaults.colors(
//            focusedTextColor = backgroundColor,
//            unfocusedTextColor = backgroundColor
//        ),
        modifier = modifier
            .padding(component.margin.toModifierPadding())
            .fillMaxWidth()
            .padding(component.padding.toPaddingValues())
    )
}