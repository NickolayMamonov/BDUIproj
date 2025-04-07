package dev.whysoezzy.bduiproj.api

import android.util.Log
import com.google.gson.GsonBuilder
import dev.whysoezzy.bduiproj.model.BDUIResponse
import dev.whysoezzy.bduiproj.model.ListComponent
import dev.whysoezzy.bduiproj.model.Screen
import dev.whysoezzy.bduiproj.utils.ComponentTypeAdapter
import dev.whysoezzy.bduiproj.utils.ManualComponentDeserializer
import dev.whysoezzy.bduiproj.utils.UIComponentWrapper
import dev.whysoezzy.bduiproj.utils.UIComponentWrapperDeserializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Клиент Retrofit для работы с API
 */
object RetrofitClient {
    private const val TAG = "RetrofitClient"
    private const val BASE_URL = "http://10.0.2.2:8080" // Локальный хост для эмулятора Android

    // Инициализация Retrofit
    private val retrofit by lazy {
        Log.d(TAG, "Initializing Retrofit with base URL: $BASE_URL")
        val gson = GsonBuilder()
            .setLenient()
            .registerTypeAdapter(UIComponentWrapper::class.java, UIComponentWrapperDeserializer())
            .registerTypeAdapter(ListComponent::class.java, ComponentTypeAdapter<ListComponent>())
            .registerTypeAdapter(Screen::class.java, ComponentTypeAdapter<Screen>())
            .registerTypeAdapter(BDUIResponse::class.java, ComponentTypeAdapter<BDUIResponse>())
            .registerTypeHierarchyAdapter(UIComponentWrapper::class.java, ManualComponentDeserializer())
            .setPrettyPrinting()
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Создание экземпляра API сервиса
    val apiService: ApiService by lazy {
        Log.d(TAG, "Creating ApiService instance")
        retrofit.create(ApiService::class.java)
    }
}