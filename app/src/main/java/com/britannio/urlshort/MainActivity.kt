package com.britannio.urlshort

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.URLUtil
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.britannio.urlshort.api.RealUrlApi
import com.britannio.urlshort.data.UrlData
import com.britannio.urlshort.db.UrlDatabase
import com.britannio.urlshort.ui.theme.UrlshortTheme
import com.britannio.urlshort.viewmodel.UrlViewModel

class MainActivity : ComponentActivity() {
    private val onCopy: (String) -> Unit = { text -> copyToClipboard(text) }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Shortened URL", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun getUrlFromClipboard(): String? {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString()
            if (text != null && URLUtil.isValidUrl(text)) {
                return text
            }
        }
        return null
    }

    private val viewModel: UrlViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val database = Room.databaseBuilder(
                    applicationContext,
                    UrlDatabase::class.java,
                    "url_database"
                ).build()
                // Read API key from BuildConfig
                val apiKey = BuildConfig.SHORT_IO_API_KEY
                return UrlViewModel(RealUrlApi(apiKey), database.urlDao()) as T
            }
        }
    }

    private fun handleIntent() {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedText ->
                        if (URLUtil.isValidUrl(sharedText)) {
                            viewModel.onUrlInputChange(sharedText)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Check clipboard for URL and prefill if found
        getUrlFromClipboard()?.let { url ->
            viewModel.onUrlInputChange(url)
        }

        // Handle shared URLs
        handleIntent()

        setContent {
            UrlshortTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UrlShortenerScreen(
                        viewModel = viewModel,
                        onCopy = onCopy
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlItem(url: UrlData, onCopy: (String) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { onCopy(url.shortenedUrl) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlShortenerScreen(
    viewModel: UrlViewModel,
    onCopy: (String) -> Unit
) {
    val urls by viewModel.urls.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("britann.io") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
        ) {
            TextField(
                value = viewModel.urlInput,
                onValueChange = viewModel::onUrlInputChange,
                label = { Text("Enter URL") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextField(
                value = viewModel.pathInput,
                onValueChange = viewModel::onPathInputChange,
                label = { Text("Custom path (optional)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = viewModel::shortenUrl,
                    enabled = !isLoading
                ) {
                    Text("Shorten URL")
                }
                Button(
                    onClick = viewModel::refreshUrls,
                    enabled = !isLoading
                ) {
                    Text("Refresh")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn {
                items(urls) { url ->
                    UrlItem(
                        url = url,
                        onCopy = onCopy
                    )
                }
            }
        }
        
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

