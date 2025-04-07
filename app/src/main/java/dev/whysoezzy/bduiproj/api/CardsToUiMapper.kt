package dev.whysoezzy.bduiproj.api

import android.util.Log
import dev.whysoezzy.bduiproj.api.model.CardResponse
import dev.whysoezzy.bduiproj.model.BDUIResponse
import dev.whysoezzy.bduiproj.model.Screen
import dev.whysoezzy.bduiproj.utils.UIComponentWrapper

/**
 * Класс для преобразования данных карточек в UI компоненты
 */
class CardsToUiMapper {
    private val TAG = "CardsToUiMapper"

    /**
     * Преобразует список карточек в BDUIResponse для отображения
     */
    fun mapCardsToBDUI(cards: List<CardResponse>): BDUIResponse {
        Log.d(TAG, "Mapping ${cards.size} cards to BDUI")

        // Создаем группированный список вложенных компонентов
        val groupedComponents = mutableListOf<UIComponentWrapper>()

        // Временная переменная для хранения текущего заголовка
        var currentHeader: String? = null
        // Временный список для хранения текущих элементов под заголовком
        val currentItems = mutableListOf<UIComponentWrapper>()

        // Обрабатываем каждую карточку
        cards.forEach { card ->
            when (card.type) {
                "HEADER" -> {
                    // Если был предыдущий заголовок, добавляем его и его элементы
                    if (currentHeader != null && currentItems.isNotEmpty()) {
                        // Создаем компонент списка с заголовком
                        groupedComponents.add(
                            createHeaderComponent(currentHeader!!)
                        )

                        // Создаем компонент списка
                        groupedComponents.add(
                            createListComponent("list_${currentHeader}", currentItems.toList())
                        )

                        // Очищаем текущие элементы
                        currentItems.clear()
                    }

                    // Устанавливаем новый заголовок
                    currentHeader = card.title
                }
                "ROW" -> {
                    // Добавляем элемент в текущий список
                    currentItems.add(
                        createRowComponent(card)
                    )
                }
            }
        }

        // Не забываем добавить последнюю группу
        if (currentHeader != null && currentItems.isNotEmpty()) {
            // Создаем компонент заголовка
            groupedComponents.add(
                createHeaderComponent(currentHeader!!)
            )

            // Создаем компонент списка
            groupedComponents.add(
                createListComponent("list_${currentHeader}", currentItems.toList())
            )
        }

        // Создаем корневой контейнер
        val rootComponent = UIComponentWrapper(
            id = "main_container",
            type = "container",
            componentType = "container",
            orientation = "vertical",
            components = groupedComponents
        )

        // Создаем экран
        val screen = Screen(
            id = "cards_screen",
            title = "Cards",
            rootComponent = rootComponent,
            backgroundColor = "#F5F5F5"
        )

        // Возвращаем BDUI ответ
        return BDUIResponse(
            id = screen.id,
            title = screen.title,
            rootComponent = screen.rootComponent,
            backgroundColor = screen.backgroundColor,
            toolbarColor = "#2196F3",
            statusBarColor = "#1976D2"
        )
    }
    
    /**
     * Создает компонент заголовка
     */
    private fun createHeaderComponent(title: String): UIComponentWrapper {
        return UIComponentWrapper(
            id = "header_${title.lowercase().replace(" ", "_")}",
            type = "text",
            componentType = "text",
            text = title,
            textSize = 18,
            textColor = "#333333",
            fontWeight = "bold",
            padding = dev.whysoezzy.bduiproj.model.Padding(16, 16, 8, 4)
        )
    }
    
    /**
     * Создает компонент элемента списка
     */
    private fun createRowComponent(card: CardResponse): UIComponentWrapper {
        return UIComponentWrapper(
            id = "row_${card.title.lowercase().replace(" ", "_")}",
            type = "container",
            componentType = "container",
            orientation = "horizontal",
            background = "#FFFFFF",
            padding = dev.whysoezzy.bduiproj.model.Padding(16, 16, 16, 16),
            margin = dev.whysoezzy.bduiproj.model.Margin(0, 0, 0, 8),
            cornerRadius = 8,
            components = listOf(
                // Изображение
                UIComponentWrapper(
                    id = "image_${card.title.lowercase().replace(" ", "_")}",
                    type = "image",
                    componentType = "image",
                    url = "android.resource://dev.whysoezzy.bduiproj/${card.image}",
                    width = 60,
                    height = 60,
                    cornerRadius = 4
                ),
                // Текст
                UIComponentWrapper(
                    id = "text_${card.title.lowercase().replace(" ", "_")}",
                    type = "text",
                    componentType = "text",
                    text = card.title,
                    textSize = 16,
                    textColor = "#333333",
                    padding = dev.whysoezzy.bduiproj.model.Padding(16, 0, 0, 0)
                )
            )
        )
    }
    
    /**
     * Создает компонент списка
     */
    private fun createListComponent(id: String, items: List<UIComponentWrapper>): UIComponentWrapper {
        return UIComponentWrapper(
            id = id,
            type = "list",
            componentType = "list",
            orientation = "vertical",
            items = items,
            dividerEnabled = true,
            dividerColor = "#E0E0E0",
            padding = dev.whysoezzy.bduiproj.model.Padding(16, 0, 16, 0)
        )
    }
}