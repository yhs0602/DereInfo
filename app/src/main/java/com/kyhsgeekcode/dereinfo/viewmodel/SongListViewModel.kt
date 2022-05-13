package com.kyhsgeekcode.dereinfo.viewmodel

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import com.kyhsgeekcode.dereinfo.model.DereDatabaseService
import com.kyhsgeekcode.dereinfo.model.MusicData
import com.kyhsgeekcode.dereinfo.model.SortType
import com.kyhsgeekcode.dereinfo.model.TW5Difficulty
import com.kyhsgeekcode.dereinfo.worker.ExportMusicWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

data class SongListUiState(
    val title: String,
)

@HiltViewModel
class SongListViewModel @Inject constructor(
    val dereDatabaseService: DereDatabaseService
) : ViewModel() {
    var selectedTabIndex by mutableStateOf(0)
    var filteredSongs by mutableStateOf(listOf<MusicData>())
    var currentDifficulty by mutableStateOf<TW5Difficulty>(TW5Difficulty.Debut)

    var databaseState by mutableStateOf(SongListUiState(""))

    private var sortType: SortType = SortType.Data
    private var sortOrderAsc: Boolean = true
    private var constraint: String = ""
    private var checkedFilters: HashMap<Int, Boolean>? = null

    fun loadDatabase(publisher: (Int, Int, MusicData?, String?) -> Unit, onFinish: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!dereDatabaseService.load(false, publisher, onFinish)) {
                onFailedLoadDatabase()
            }
        }
    }

    fun refreshCache(publisher: (Int, Int, MusicData?, String?) -> Unit, onFinish: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!dereDatabaseService.refreshCache(publisher, onFinish)) {
                onFailedLoadDatabase()
            }
        }
    }

    fun onFailedLoadDatabase() {

    }

    fun exportWorkRequest(uri: Uri) = OneTimeWorkRequestBuilder<ExportMusicWorker>()
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .setInputData(
            Data.Builder()
                .putString(ExportMusicWorker.KEY_OUTPUT_URI, uri.toString())
                .putString(
                    ExportMusicWorker.KEY_INPUT_FOLDER,
                    dereDatabaseService.musicFolder.path
                )
                .build()
        )
        .build()


    fun reallyExportTw(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                contentResolver.openFileDescriptor(uri, "w")?.use {
                    FileOutputStream(it.fileDescriptor).use { fos ->
                        val list = filteredSongs
//                        dereDatabaseService.exportTW(
//                            list,
//                            checkedDifficulties,
//                            fos
//                        ) { progress, message ->
//                            withContext(Dispatchers.Main) {
//                                circularType.setProgressMax(list.size)
//                                snackProgressBarManager.setProgress(progress)
//                                if (message != null) {
//                                    circularType.setMessage(message)
//                                }
//                                snackProgressBarManager.updateTo(circularType)
//                            }
//                        }
                    }
                }
            } catch (e: FileNotFoundException) {
                Timber.d(e, "File not found")
            } catch (e: IOException) {
                Timber.d(e, "IOExcpetipon")
            }
        }
    }

    fun onChangeSortOption(sortType: SortType, sortOrderAsc: Boolean) {
        TODO("Not yet implemented")
    }

    fun onTabClicked(newIndex: Int) {
        selectedTabIndex = newIndex
    }
}