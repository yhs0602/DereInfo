package com.kyhsgeekcode.dereinfo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kyhsgeekcode.dereinfo.database.SongDatabase
import com.kyhsgeekcode.dereinfo.entity.SongEntity
import com.kyhsgeekcode.dereinfo.repository.SongListRepository

class SongListViewModel(application: Application) : AndroidViewModel(application) {
    val repository: SongListRepository =
        SongListRepository(SongDatabase.getDatabase(application, viewModelScope))
    var allSongs: LiveData<List<SongEntity>> = repository.allSongs

}