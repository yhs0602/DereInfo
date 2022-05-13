package com.kyhsgeekcode.dereinfo.ui

import android.app.Instrumentation
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.kyhsgeekcode.dereinfo.R.id.*
import com.kyhsgeekcode.dereinfo.enums.CircleType
import com.kyhsgeekcode.dereinfo.enums.GameMode
import com.kyhsgeekcode.dereinfo.model.MusicData
import com.kyhsgeekcode.dereinfo.model.TW5Difficulty
import com.kyhsgeekcode.dereinfo.viewmodel.SongListViewModel
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import com.xeinebiu.suspend.dialogs.SuspendAlertDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_song_list.*
import kotlinx.android.synthetic.main.song_list.*
import kotlinx.coroutines.*
import timber.log.Timber


/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [SongDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
@AndroidEntryPoint
class SongListActivity : AppCompatActivity() {
    private val songListViewModel: SongListViewModel by viewModels()

    private val snackProgressBarManager by lazy {
        SnackProgressBarManager(
            mainListLayout,
            lifecycleOwner = this
        )
    }
    val circularType =
        SnackProgressBar(SnackProgressBar.TYPE_CIRCULAR, "Loading...")
            .setIsIndeterminate(false)
            .setAllowUserInput(false)


    val RC_ACTIVITY_FOR_RESULT = 101
    internal var deffered = CompletableDeferred<Instrumentation.ActivityResult>()

    private fun startForResultAsync(intent: Intent): Deferred<Instrumentation.ActivityResult> {
        if (deffered.isActive)
            deffered.cancel()
        deffered = CompletableDeferred()
        startActivityForResult(intent, RC_ACTIVITY_FOR_RESULT)
        return deffered
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_ACTIVITY_FOR_RESULT) {
            deffered.complete(
                Instrumentation.ActivityResult(
                    resultCode,
                    data
                )
            )
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false
    private val publisher: (Int, Int, MusicData?, String?) -> Unit =
        { total, progress, info, message ->
            CoroutineScope(Dispatchers.Main).launch {
                circularType.setProgressMax(total)
//                if (info != null)
//                    adapter.addItem(info)
                snackProgressBarManager.setProgress(progress)
                if (message != null) {
                    circularType.setMessage(message)
                }
                snackProgressBarManager.updateTo(circularType)
            }
        }
    val onFinish: () -> Unit = {
        snackProgressBarManager.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column {
                TabTitles(songListViewModel.selectedTabIndex, songListViewModel::onTabClicked)
                LazyColumn {
                    items(songListViewModel.filteredSongs) { item ->
                        OneSongRow(item, songListViewModel.currentDifficulty)
                    }
                }
            }
        }

        tl_song_list_modes.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Timber.d("OnSelectedTab")
//                val tag = tab?.view?.id as? String ?: "normal"
                val gamemode = GameMode.fromTabIndex(tab?.position ?: 0)
                Timber.d("GameMode: $gamemode")
                refreshMode(gamemode ?: GameMode.NORMAL)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        snackProgressBarManager.show(circularType, SnackProgressBarManager.LENGTH_INDEFINITE)
        try {
            songListViewModel.loadDatabase(publisher, onFinish)
        } catch (e: Exception) {
            Timber.e(e, "Failed to find database")
            Toast.makeText(this, "Failed to find database", Toast.LENGTH_SHORT).show()
            guideInstallDeresute()
            finish()
            return
        }
    }


    private fun refreshMode(gamemode: GameMode) {
//        adapter.userFilter.shouldHaveGrand = gamemode == GameMode.GRAND
//        adapter.userFilter.shouldHaveMasterPlus = gamemode == GameMode.MASTERPLUS
//        adapter.userFilter.shouldHaveSmart = gamemode == GameMode.SMART
//        adapter.userFilter.shouldHaveWitch = gamemode == GameMode.WITCH
//
//        adapter.gameMode = gamemode
//        song_list.adapter = adapter
//        adapter.notifyDataSetChanged()
//        adapter.filter?.filter("")
    }

    private fun refreshCache(
        publisher: (Int, Int, MusicData?, String?) -> Unit,
        onFinish: () -> Unit
    ) {
        snackProgressBarManager.show(circularType, SnackProgressBarManager.LENGTH_INDEFINITE)
        songListViewModel.refreshCache(publisher, onFinish)
    }

    private fun onFailedLoadDatabase() {
        runOnUiThread {
            Snackbar.make(
                mainListLayout,
                "Please install deresute first and download resources.",
                Snackbar.LENGTH_LONG
            )
                .setAction("Play store") {
                    guideInstallDeresute()
                }
                .show()
        }
    }


    private fun guideInstallDeresute() {
        val appPackageName = "jp.co.bandainamcoent.BNEI0242"
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        snackProgressBarManager.disable()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.itemId
        when (id) {
            app_bar_sort -> {
                val sortAlertDialogFragment = SortAlertDialogFragment()
//                sortAlertDialogFragment.show(supportFragmentManager, "sortFragment")
            }
            app_bar_filter -> {
//                val filterAlertDialogFragment = FilterAlertDialogFragment(checkedFilters)
//                filterAlertDialogFragment.show(supportFragmentManager, "filterFragment")
            }
            app_bar_refresh -> {
                refreshCache(publisher, onFinish)
            }
            app_bar_units -> {
                startActivity(Intent(this, UnitListActivity::class.java))
            }
            export_all_tw -> {
                exportTw()
            }
            export_everything -> {
                exportMusic()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exportMusicBackground(uri: Uri) {
        val exportWorkRequest = songListViewModel.exportWorkRequest(uri)
        WorkManager.getInstance(this)
            .enqueueUniqueWork("music export", ExistingWorkPolicy.REPLACE, exportWorkRequest)
    }

    private fun exportMusic() {
        CoroutineScope(Dispatchers.Main).launch {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/zip"
                putExtra(Intent.EXTRA_TITLE, "music.zip")

                // Optionally, specify a URI for the directory that should be opened in
                // the system file picker before your app creates the document.
                //                            putExtra(DocumentsContract.EXTRA_INITIAL_URI, null)
            }
            val activityResult = startForResultAsync(intent).await()
            val code = activityResult.resultCode
            val data = activityResult.resultData
            data?.data?.also { uri ->
                exportMusicBackground(uri)
            }
        }
    }


    private fun exportTw() {
        val items = TW5Difficulty.values().map { it.name }.toTypedArray()
        val initialChecked = BooleanArray(items.size)
        CoroutineScope(Dispatchers.Main).launch {
            val checked = SuspendAlertDialog.setMultiChoiceItems(
                positiveButtonText = "OK",
                negativeButtonText = "Cancel",
                items = SuspendAlertDialog.MultiChoiceItems(
                    items = items.toList(),
                    checked = initialChecked.toList()
                )
            ) {
                AlertDialog.Builder(this@SongListActivity).setTitle("Select difficulty")
            }
            val checkedDifficulties = checked.checked.withIndex().filter { it.value }
                .map { TW5Difficulty.values()[it.index] }
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/zip"
                putExtra(Intent.EXTRA_TITLE, "twFiles.zip")

                // Optionally, specify a URI for the directory that should be opened in
                // the system file picker before your app creates the document.
                //                            putExtra(DocumentsContract.EXTRA_INITIAL_URI, null)
            }
            val activityResult = startForResultAsync(intent).await()
            val code = activityResult.resultCode
            val data = activityResult.resultData
            data?.data?.also { uri ->
                snackProgressBarManager.show(
                    circularType,
                    SnackProgressBarManager.LENGTH_INDEFINITE
                )
                songListViewModel.reallyExportTw(contentResolver, uri)
                snackProgressBarManager.dismiss()
            }
        }
    }

//    private fun setupRecyclerView(recyclerView: RecyclerView): SongRecyclerViewAdapter {
//        val adapter = SongRecyclerViewAdapter(this, twoPane)
//        adapter.userFilter.addFilter(
//            CircleType.All,
//            CircleType.Cute,
//            CircleType.Cool,
//            CircleType.Passion
//        )
//        recyclerView.adapter = adapter
//        recyclerView.addItemDecoration(
//            DividerItemDecoration(
//                this,
//                DividerItemDecoration.VERTICAL
//            )
//        )
//        return adapter
//    }

//    private fun sortList() {
//        Toast.makeText(this, "Sort by " + sortType.name, Toast.LENGTH_SHORT).show()
//        adapter.sortBy(sortType, sortOrderAsc)
//    }

//    override fun onDialogPositiveClick(dialog: DialogFragment?, checked: Map<Int, Boolean>) {
//        Timber.d("Permitted:" + checked.toList().joinToString())
//        checkedFilters = HashMap()
//        checkedFilters!!.putAll(checked)
//        val permittedType: MutableList<CircleType> = ArrayList()
//        if (checked[filterCBTypeAllCheck]!! || checked[filterCBAllType]!!) {
//            permittedType.add(CircleType.All)
//        }
//        if (checked[filterCBTypeAllCheck]!! || checked[filterCBCute]!!) {
//            permittedType.add(CircleType.Cute)
//        }
//        if (checked[filterCBTypeAllCheck]!! || checked[filterCBCool]!!) {
//            permittedType.add(CircleType.Cool)
//        }
//        if (checked[filterCBTypeAllCheck]!! || checked[filterCBPassion]!!) {
//            permittedType.add(CircleType.Passion)
//        }
//        Timber.d("Permitted2:" + permittedType.toTypedArray().joinToString())
//        adapter.userFilter.addFilter(*permittedType.toTypedArray())
//        adapter.userFilter.shouldHaveMasterPlus = checked[filterCBMasterPlus] ?: false
//        adapter.userFilter.shouldHaveSmart = checked[filterCBSmart] ?: false
//        adapter.userFilter.shouldHaveGrand = checked[filterCBGrand] ?: false
//        adapter.userFilter.shouldBeStarred = checked[filterCBStarred] ?: false
//        adapter.filter?.filter(constraint)
//        //sortList()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt(SortType_KEY, sortType.value)
//        outState.putString(Constraint_KEY, constraint)
//        if (checkedFilters != null)
//            outState.putSerializable(Checked_KEY, checkedFilters as Serializable)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        sortType = SortType.getByValue(savedInstanceState.getInt(SortType_KEY, 0))
//        constraint = savedInstanceState.getString(Constraint_KEY, "")
//        try {
//            checkedFilters =
//                savedInstanceState.getSerializable(Checked_KEY) as HashMap<Int, Boolean>?
//            if (checkedFilters == null)
//                checkedFilters = HashMap()
//            if (checkedFilters?.isEmpty() == true) {
//                checkedFilters!![filterCBTypeAllCheck] = true
//                checkedFilters!![filterCBCute] = true
//                checkedFilters!![filterCBCool] = true
//                checkedFilters!![filterCBPassion] = true
//                checkedFilters!![filterCBAllType] = true
//                checkedFilters!![filterCBMasterPlus] = true
//            }
//            onDialogPositiveClick(null, checkedFilters!!)
//            adapter.filter?.filter(constraint)
//        } catch (e: Exception) {
//            Log.e(TAG, "", e)
//        }
//    }
//
//    override fun onDialogPositiveClick(dialog: DialogFragment?, item: Int, ascending: Boolean) {
//        val sortType = SortType.getByValue(item)
//        val sortOrderAsc = ascending
//        songListViewModel.onChangeSortOption(sortType, sortOrderAsc)
//        sortList()
//    }
//
//    override fun onDialogNegativeClick(dialog: DialogFragment) {
//
//    }
}

@Composable
private fun TabTitles(selectedTabIndex: Int, onTabClicked: (Int) -> Unit) {
    val titles = listOf("Normal", "Master+", "Witch", "Smart", "Grand")

    TabRow(
        selectedTabIndex = selectedTabIndex,
        divider = {},
//                backgroundColor = Colors.Grey[-1]!!,
//                contentColor = Colors.Grey[7]!!,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
//                        color = Colors.SLBlue
            )
        }
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                text = {
                    Text(
                        title,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
//                                color = Colors.Grey[7]!!,
                        fontWeight = if (selectedTabIndex == index) FontWeight.W700 else FontWeight.W400
                    )
                },
                selected = selectedTabIndex == index,
                onClick = { onTabClicked(index) },
            )
        }
    }
}

@Composable
fun OneSongRow(item: MusicData, currentDifficulty: TW5Difficulty) {
    Column {
        Row {
            Text(text = item.name, modifier = Modifier.weight(1f))
//            Text(text = "lv ${item.}") // level
            Icon(Icons.Default.Home, contentDescription = "Hello")
//            Text(text = "${item.}") // note count
        }
        Text(text = item.composer)
        DifficultyPane(item, currentDifficulty)
    }
}

@Composable
fun DifficultyPane(item: MusicData, currentDifficulty: TW5Difficulty) {

}