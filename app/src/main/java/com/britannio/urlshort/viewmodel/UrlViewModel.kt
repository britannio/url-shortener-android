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
