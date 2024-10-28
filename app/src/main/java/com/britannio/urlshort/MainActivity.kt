package com.britannio.urlshort

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.britannio.urlshort.api.MockUrlApi
import com.britannio.urlshort.data.UrlData
import com.britannio.urlshort.db.UrlDatabase
import com.britannio.urlshort.ui.theme.UrlshortTheme
import com.britannio.urlshort.viewmodel.UrlViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: UrlViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = Room.databaseBuilder(
                    applicationContext,
                    UrlDatabase::class.java,
                    "url_database"
                ).build()
                return UrlViewModel(MockUrlApi(), database.urlDao()) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UrlshortTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UrlShortenerScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun UrlShortenerScreen(viewModel: UrlViewModel) {
    val urls by viewModel.urls.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = viewModel.urlInput,
            onValueChange = viewModel::onUrlInputChange,
            label = { Text("Enter URL") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TextField(
            value = viewModel.pathInput,
            onValueChange = viewModel::onPathInputChange,
            label = { Text("Custom path (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = viewModel::shortenUrl) {
                Text("Shorten URL")
            }
            Button(onClick = viewModel::refreshUrls) {
                Text("Refresh")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn {
            items(urls) { url ->
                UrlItem(url)
            }
        }
    }
}

@Composable
fun UrlItem(url: UrlData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Original: ${url.originalUrl}")
            Text(text = "Shortened: ${url.shortenedUrl}")
        }
    }
}
