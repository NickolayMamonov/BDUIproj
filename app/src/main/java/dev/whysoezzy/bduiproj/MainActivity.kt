package dev.whysoezzy.bduiproj

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.whysoezzy.bduiproj.api.BDUIRepository
import dev.whysoezzy.bduiproj.model.Screen
import dev.whysoezzy.bduiproj.model.SimpleScreenResponse
import dev.whysoezzy.bduiproj.ui.SimpleCardsScreen
import dev.whysoezzy.bduiproj.ui.renders.SDUIScreen
import dev.whysoezzy.bduiproj.ui.theme.BDUIprojTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val bduiRepository = BDUIRepository()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BDUIprojTheme {
                val coroutineScope = rememberCoroutineScope()
                var simpleScreen by remember { mutableStateOf<SimpleScreenResponse?>(null) }
                var isLoading by remember { mutableStateOf(true) }
                var error by remember { mutableStateOf<String?>(null) }
                
                // Загружаем UI при старте
                LaunchedEffect(key1 = true) {
                    loadSimpleCardsScreen(
                        coroutineScope = coroutineScope, 
                        isLoading = { loading -> isLoading = loading },
                        callback = { screen, errorMessage ->
                            simpleScreen = screen
                            error = errorMessage
                        }
                    )
                }
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        
                        // Показываем загрузку
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                        
                        // Показываем ошибку
                        error?.let {
                            Text(
                                text = "Ошибка: $it",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        
                        // Отображаем упрощенный экран
                        simpleScreen?.let {
                            SimpleCardsScreen(
                                screen = it,
                                onNavigate = { url ->
                                    handleSimpleNavigation(
                                        url, 
                                        coroutineScope, 
                                        isLoading = { loading -> isLoading = loading },
                                        callback = { screen, errorMessage ->
                                            simpleScreen = screen
                                            error = errorMessage
                                        }
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Загружает упрощенный экран со списком карточек
     */
    private fun loadSimpleCardsScreen(
        coroutineScope: kotlinx.coroutines.CoroutineScope,
        isLoading: (Boolean) -> Unit,
        callback: (SimpleScreenResponse?, String?) -> Unit
    ) {
        isLoading(true)
        Log.d(TAG, "Loading simple cards screen")
        
        coroutineScope.launch {
            try {
                val result = bduiRepository.getSimpleCardsScreen()
                result.fold(
                    onSuccess = { screen ->
                        Log.d(TAG, "Simple cards screen loaded successfully")
                        callback(screen, null)
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Error loading simple cards screen", exception)
                        callback(null, exception.message ?: "Неизвестная ошибка")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading simple cards screen", e)
                callback(null, e.message ?: "Неизвестная ошибка")
            } finally {
                isLoading(false)
            }
        }
    }
    
    /**
     * Загружает упрощенный экран с деталями карточки
     */
    private fun loadSimpleCardDetailScreen(
        cardId: Int,
        coroutineScope: kotlinx.coroutines.CoroutineScope,
        isLoading: (Boolean) -> Unit,
        callback: (SimpleScreenResponse?, String?) -> Unit
    ) {
        isLoading(true)
        Log.d(TAG, "Loading simple card detail screen for id: $cardId")
        
        coroutineScope.launch {
            try {
                val result = bduiRepository.getSimpleCardDetailScreen(cardId)
                result.fold(
                    onSuccess = { screen ->
                        Log.d(TAG, "Simple card detail screen loaded successfully")
                        callback(screen, null)
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Error loading simple card detail screen", exception)
                        callback(null, exception.message ?: "Неизвестная ошибка")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading simple card detail screen", e)
                callback(null, e.message ?: "Неизвестная ошибка")
            } finally {
                isLoading(false)
            }
        }
    }
    
    /**
     * Обрабатывает навигацию для упрощенного UI
     */
    private fun handleSimpleNavigation(
        url: String,
        coroutineScope: kotlinx.coroutines.CoroutineScope,
        isLoading: (Boolean) -> Unit,
        callback: (SimpleScreenResponse?, String?) -> Unit
    ) {
        Log.d(TAG, "Simple navigation requested to: $url")
        
        when {
            url == "/cards" -> {
                loadSimpleCardsScreen(coroutineScope, isLoading, callback)
            }
            url.startsWith("/card/") -> {
                try {
                    val cardId = url.substringAfter("/card/").toInt()
                    loadSimpleCardDetailScreen(cardId, coroutineScope, isLoading, callback)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing card ID from URL: $url", e)
                    callback(null, "Некорректный URL: $url")
                }
            }
            else -> {
                Log.e(TAG, "Unknown navigation URL: $url")
                callback(null, "Неизвестный путь: $url")
            }
        }
    }
}