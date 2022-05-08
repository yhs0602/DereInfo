package com.kyhsgeekcode.dereinfo.viewmodel

import androidx.lifecycle.ViewModel
import com.kyhsgeekcode.dereinfo.model.DereDatabaseService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongDetailViewModel @Inject constructor(val dereDatabaseService: DereDatabaseService) :
    ViewModel() {
}