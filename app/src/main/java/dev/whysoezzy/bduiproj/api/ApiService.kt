package dev.whysoezzy.bduiproj.api

import dev.whysoezzy.bduiproj.api.model.CardResponse
import dev.whysoezzy.bduiproj.api.model.TextRequest
import dev.whysoezzy.bduiproj.model.BDUIResponse
import dev.whysoezzy.bduiproj.model.SimpleScreenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Интерфейс для взаимодействия с API бэкенда
 */
interface ApiService {
    
    /**
     * Получение списка всех карточек
     */
    @GET("/api/cards")
    suspend fun getAllCards(): Response<List<CardResponse>>
    
    /**
     * Получение определенного количества карточек
     */
    @GET("/api/cards/{count}")
    suspend fun getCards(@Path("count") count: Int): Response<List<CardResponse>>
    
    /**
     * Получение одной карточки по ID
     */
    @GET("/api/card/{id}")
    suspend fun getCard(@Path("id") id: Int): Response<CardResponse>
    
    /**
     * Поиск карточек по запросу
     */
    @GET("/api/cards/search")
    suspend fun searchCards(@Query("query") query: String): Response<List<CardResponse>>
    
    /**
     * Отправка текстового запроса
     */
    @POST("/api/text")
    suspend fun sendText(@Body textRequest: TextRequest): Response<Any>
    
    /**
     * Получение экрана приветствия
     */
    @GET("/api/ui/welcome")
    suspend fun getWelcomeScreen(): Response<BDUIResponse>
    
    /**
     * Получение экрана с карточками
     */
    @GET("/api/ui/cards")
    suspend fun getCardsScreen(): Response<BDUIResponse>
    
    /**
     * Получение экрана деталей карточки
     */
    @GET("/api/ui/card/{id}")
    suspend fun getCardDetailScreen(@Path("id") id: Int): Response<BDUIResponse>
    
    // Упрощенные эндпоинты
    /**
     * Получение упрощенного экрана с карточками
     */
    @GET("/api/simple/cards")
    suspend fun getSimpleCardsScreen(): Response<SimpleScreenResponse>
    
    /**
     * Получение упрощенного экрана деталей карточки
     */
    @GET("/api/simple/card/{id}")
    suspend fun getSimpleCardDetailScreen(@Path("id") id: Int): Response<SimpleScreenResponse>
}