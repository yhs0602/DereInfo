package com.kyhsgeekcode.dereinfo.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SongListUiState(
    val title: String,
)

@HiltViewModel
class SongListViewModel @Inject constructor(
    val dereDatabaseHelper: DereDatabaseHelper
) : ViewModel() {
    var databaseState by mutableStateOf<SongListUiState>()

    fun loadDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!dereDatabaseHelper.load(false, publisher, onFinish)) {
                onFailedLoadDatabase()
            }
        }
    }
}