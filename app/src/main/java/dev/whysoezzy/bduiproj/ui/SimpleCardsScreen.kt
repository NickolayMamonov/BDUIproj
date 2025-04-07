package dev.whysoezzy.bduiproj.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.whysoezzy.bduiproj.model.SimpleCardResponse
import dev.whysoezzy.bduiproj.model.SimpleScreenResponse
import dev.whysoezzy.bduiproj.ui.utils.parseColor
import dev.whysoezzy.bduiproj.util.ImageMapper

private const val TAG = "SimpleCardsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleCardsScreen(
    screen: SimpleScreenResponse,
    onNavigate: (String) -> Unit
) {
    Log.d(TAG, "Rendering simple screen: ${screen.id}, items: ${screen.items.size}")
    
    val backgroundColor = screen.backgroundColor.let { parseColor(it) } ?: MaterialTheme.colorScheme.background
    val toolbarColor = screen.toolbarColor.let { parseColor(it) } ?: MaterialTheme.colorScheme.primary
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Toolbar
        TopAppBar(
            title = { Text(text = screen.title, color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = toolbarColor
            )
        )
        
        // Список карточек
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(screen.items) { card ->
                when (card.type) {
                    "HEADER" -> {
                        HeaderItem(card)
                    }
                    "ROW" -> {
                        CardItem(card, onNavigate)
                    }
                    "DETAIL" -> {
                        DetailItem(card)
                    }
                    "BUTTON" -> {
                        ButtonItem(card, onNavigate)
                    }
                    else -> {
                        Log.w(TAG, "Unknown card type: ${card.type}")
                        Text(
                            text = "Неизвестный тип карточки: ${card.type}",
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun HeaderItem(card: SimpleCardResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = card.title,
            color = Color(0xFF2196F3),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CardItem(card: SimpleCardResponse, onNavigate: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                card.actionUrl?.let { url ->
                    Log.d(TAG, "Navigating to: $url")
                    onNavigate(url)
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Изображение
            Image(
                painter = painterResource(id = ImageMapper.getDrawableResource(card.image)),
                contentDescription = card.title,
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            
            // Текст
            Text(
                text = card.title,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun DetailItem(card: SimpleCardResponse) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = card.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Изображение
        Image(
            painter = painterResource(id = ImageMapper.getDrawableResource(card.image)),
            contentDescription = card.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        
        // Описание
        Text(
            text = "Подробное описание карточки. Здесь может быть любая дополнительная информация.",
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
        )
    }
}

@Composable
private fun ButtonItem(card: SimpleCardResponse, onNavigate: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .clickable {
                    card.actionUrl?.let { url ->
                        Log.d(TAG, "Button clicked, navigating to: $url")
                        onNavigate(url)
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2196F3)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = card.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}