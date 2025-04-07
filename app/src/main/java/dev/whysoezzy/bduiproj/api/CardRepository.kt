package dev.whysoezzy.bduiproj.api

import android.util.Log
import dev.whysoezzy.bduiproj.api.model.CardResponse
import dev.whysoezzy.bduiproj.api.model.TextRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Репозиторий для работы с карточками через API
 */
class CardRepository {
    private val apiService = RetrofitClient.apiService
    private val TAG = "CardRepository"

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
                        Result.success(body)
                    } else {
                        Log.e(TAG, "API call returned null body")
                        Result.failure(Exception("Response body is null"))
                    }
                } else {
                    Log.e(TAG, "API call failed with code: ${response.code()}, message: ${response.message()}")
                    Result.failure(Exception("API call failed with code: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during API call", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Получение всех карточек
     */
    suspend fun getAllCards(): Result<List<CardResponse>> {
        Log.d(TAG, "Getting all cards")
        return safeApiCall { apiService.getAllCards() }
    }

    /**
     * Получение ограниченного количества карточек
     */
    suspend fun getCards(count: Int): Result<List<CardResponse>> {
        Log.d(TAG, "Getting $count cards")
        return safeApiCall { apiService.getCards(count) }
    }

    /**
     * Получение карточки по ID
     */
    suspend fun getCard(id: Int): Result<CardResponse> {
        Log.d(TAG, "Getting card with id: $id")
        return safeApiCall { apiService.getCard(id) }
    }

    /**
     * Поиск карточек по запросу
     */
    suspend fun searchCards(query: String): Result<List<CardResponse>> {
        Log.d(TAG, "Searching cards with query: $query")
        return safeApiCall { apiService.searchCards(query) }
    }

    /**
     * Отправка текстового запроса
     */
    suspend fun sendText(text: String): Result<Any> {
        Log.d(TAG, "Sending text: $text")
        return safeApiCall { apiService.sendText(TextRequest(text)) }
    }
}