package com.kyhsgeekcode.dereinfo.viewmodel

import androidx.lifecycle.ViewModel
import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongDetailViewModel @Inject constructor(val dereDatabaseHelper: DereDatabaseHelper) :
    ViewModel() {
}