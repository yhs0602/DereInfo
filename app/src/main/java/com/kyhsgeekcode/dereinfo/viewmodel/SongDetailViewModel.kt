package com.kyhsgeekcode.dereinfo.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kyhsgeekcode.dereinfo.model.DereDatabaseService
import com.kyhsgeekcode.dereinfo.model.MusicData
import com.kyhsgeekcode.dereinfo.model.OneMusic
import com.kyhsgeekcode.dereinfo.model.TW5Difficulty
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongDetailViewModel @Inject constructor(
    val dereDatabaseService: DereDatabaseService, savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    private var item: MusicData? = null
    private var oneMusic: OneMusic? = null
    private var difficulty: TW5Difficulty = TW5Difficulty.Debut
    private var bitmap: Bitmap? = null
    init {

    }
}