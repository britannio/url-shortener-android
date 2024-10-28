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
