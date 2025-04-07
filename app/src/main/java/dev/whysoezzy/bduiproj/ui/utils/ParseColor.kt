package dev.whysoezzy.bduiproj.ui.utils

import androidx.compose.ui.graphics.Color
import java.util.Locale

fun parseColor(colorString: String): Color {
    return try {
        if (colorString.startsWith("#")) {
            Color(android.graphics.Color.parseColor(colorString))
        } else {
            // Предопределенные цвета могут обрабатываться здесь
            when (colorString.lowercase(Locale.getDefault())) {
                "red" -> Color.Red
                "green" -> Color.Green
                "blue" -> Color.Blue
                "white" -> Color.White
                "black" -> Color.Black
                "gray" -> Color.Gray
                else -> Color.Black
            }
        }
    } catch (e: Exception) {
        Color.Black
    }
}