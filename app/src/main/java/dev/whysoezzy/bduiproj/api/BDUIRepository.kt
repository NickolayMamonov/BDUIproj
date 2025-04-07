package dev.whysoezzy.bduiproj.api

import android.util.Log
import dev.whysoezzy.bduiproj.model.BDUIResponse
import dev.whysoezzy.bduiproj.model.Screen
import dev.whysoezzy.bduiproj.model.SimpleScreenResponse
import dev.whysoezzy.bduiproj.model.toScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Репозиторий для работы с BDUI
 */
class BDUIRepository {
    private val apiService = RetrofitClient.apiService
    private val TAG = "BDUIRepository"

    /**
     * Функция для выполнения безопасного API запроса
     */
    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    Log.d(TAG, "API call successful")
                    val body = response.body()
                    if (body != null) {
                        Log.d(TAG, "Received response: $body")
                        if (body is BDUIResponse) {
                            Log.d(TAG, "Root component: ${body.rootComponent?.id}, type: ${body.rootComponent?.type}")
                            if (body.rootComponent?.type == "list") {
                                Log.d(TAG, "List has ${body.rootComponent?.items?.size ?: 0} items")
                            } else if (body.rootComponent?.type == "container") {
                                Log.d(TAG, "Container has ${body.rootComponent?.components?.size ?: 0} components")
                            }
                        } else if (body is SimpleScreenResponse) {
                            Log.d(TAG, "Simple screen response: ${body.id}, items: ${body.items.size}")
                        }
                        return@withContext Result.success(body)
                    } else {
                        Log.e(TAG, "API call returned null body")
                        return@withContext Result.failure(Exception("Response body is null"))
                    }
                } else {
                    Log.e(TAG, "API call failed with code: ${response.code()}, message: ${response.message()}")
                    return@withContext Result.failure(Exception("API call failed with code: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during API call", e)
                return@withContext Result.failure(e)
            }
        }
    }

    /**
     * Получение экрана приветствия
     */
    suspend fun getWelcomeScreen(): Result<Screen> {
        Log.d(TAG, "Getting welcome screen")
        return safeApiCall { apiService.getWelcomeScreen() }.map { it.toScreen() }
    }

    /**
     * Получение экрана с карточками
     */
    suspend fun getCardsScreen(): Result<Screen> {
        Log.d(TAG, "Getting cards screen")
        return safeApiCall { apiService.getCardsScreen() }.map { it.toScreen() }
    }
    
    /**
     * Получение экрана деталей карточки
     */
    suspend fun getCardDetailScreen(id: Int): Result<Screen> {
        Log.d(TAG, "Getting card detail screen for id: $id")
        return safeApiCall { apiService.getCardDetailScreen(id) }.map { it.toScreen() }
    }
    
    /**
     * Получение упрощенного экрана с карточками
     */
    suspend fun getSimpleCardsScreen(): Result<SimpleScreenResponse> {
        Log.d(TAG, "Getting simple cards screen")
        return safeApiCall { apiService.getSimpleCardsScreen() }
    }
    
    /**
     * Получение упрощенного экрана деталей карточки
     */
    suspend fun getSimpleCardDetailScreen(id: Int): Result<SimpleScreenResponse> {
        Log.d(TAG, "Getting simple card detail screen for id: $id")
        return safeApiCall { apiService.getSimpleCardDetailScreen(id) }
    }
}