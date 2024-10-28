package com.britannio.urlshort.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.britannio.urlshort.api.MockUrlApi
import com.britannio.urlshort.data.UrlData
import com.britannio.urlshort.db.UrlDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UrlViewModel(
    private val api: MockUrlApi,
    private val dao: UrlDao
) : ViewModel() {
    private val _urls = dao.getAllUrls().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val urls: StateFlow<List<UrlData>> = _urls

    var urlInput by mutableStateOf("")
        private set
    var pathInput by mutableStateOf("")
        private set

    init {
        refreshUrls()
    }

    fun onUrlInputChange(value: String) {
        urlInput = value
    }

    fun onPathInputChange(value: String) {
        pathInput = value
    }

    fun shortenUrl() {
        if (urlInput.isBlank()) return
        viewModelScope.launch {
            val shortened = api.shortenUrl(urlInput, pathInput.takeIf { it.isNotBlank() })
            dao.insertUrl(shortened)
            urlInput = ""
            pathInput = ""
            refreshUrls()
        }
    }

    fun refreshUrls() {
        viewModelScope.launch {
            val recentUrls = api.getRecentUrls()
            dao.insertUrls(recentUrls)
        }
    }
}
package com.britannio.urlshort.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.britannio.urlshort.api.RealUrlApi
import com.britannio.urlshort.data.UrlData
import com.britannio.urlshort.db.UrlDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UrlViewModel(
    private val urlApi: RealUrlApi,
    private val urlDao: UrlDao
) : ViewModel() {

    var urlInput by mutableStateOf("")
        private set

    var pathInput by mutableStateOf("")
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val urls = urlDao.getAll()

    fun onUrlInputChange(url: String) {
        urlInput = url
    }

    fun onPathInputChange(path: String) {
        pathInput = path
    }

    fun shortenUrl() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val shortenedUrl = urlApi.shortenUrl(urlInput, pathInput)
                urlDao.insert(UrlData(originalUrl = urlInput, shortenedUrl = shortenedUrl))
                urlInput = ""
                pathInput = ""
                refreshUrls()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshUrls() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val urls = urlApi.getUrls()
                urlDao.deleteAll()
                urlDao.insertAll(urls)
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
