package dev.whysoezzy.bduiproj.ui.renders

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.whysoezzy.bduiproj.ui.utils.parseColor
import dev.whysoezzy.bduiproj.model.Screen

private const val TAG = "SDUIScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SDUIScreen(
    screen: Screen?,
    onNavigate: (String) -> Unit,
    onApiCall: (String, Map<String, String>?) -> Unit
) {
    // Проверка на null
    if (screen == null) {
        Log.e(TAG, "Screen is null")
        ErrorContent("Screen data is missing")
        return
    }

    // Логирование для отладки
    Log.d(TAG, "Rendering screen: ${screen.id}, rootComponent: ${screen.rootComponent != null}")
    screen.rootComponent?.let {
        Log.d(TAG, "Root component type: ${it.type ?: it.componentType ?: "unknown"}")
        Log.d(TAG, "Root component id: ${it.id}")
    }

    val backgroundColor =
        screen.backgroundColor?.let { parseColor(it) } ?: MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Toolbar if title is provided
        screen.title?.let { title ->
            val toolbarColor =
                screen.toolbarColor?.let { parseColor(it) } ?: MaterialTheme.colorScheme.primary

            TopAppBar(
                title = { Text(title) },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = toolbarColor,
//                    titleContentColor = Color.White
//                )
            )
        }

        // Проверка rootComponent на null
        if (screen.rootComponent == null) {
            Log.e(TAG, "Root component is null for screen: ${screen.id}")
            ErrorContent("Screen has no content to display")
            return
        }

        // Render root component
        RenderComponent(
            component = screen.rootComponent,
            onNavigate = onNavigate,
            onApiCall = onApiCall,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ErrorContent(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error: $message",
            color = Color.Red,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}