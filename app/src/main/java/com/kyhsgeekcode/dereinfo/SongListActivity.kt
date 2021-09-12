package com.kyhsgeekcode.dereinfo

import android.app.Instrumentation
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.kyhsgeekcode.dereinfo.databinding.ActivitySongListBinding
import com.kyhsgeekcode.dereinfo.databinding.SongListBinding
import com.kyhsgeekcode.dereinfo.model.*
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import kotlinx.coroutines.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable


/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [SongDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class SongListActivity : AppCompatActivity(),
    FilterAlertDialogFragment.FilterDialogListener,
    SortAlertDialogFragment.SortDialogListener {
    val TAG = "SongListActivity"

    private lateinit var binding: ActivitySongListBinding
    private lateinit var listbinding: SongListBinding

    private val snackProgressBarManager by lazy {
        SnackProgressBarManager(
            binding.mainListLayout,
            lifecycleOwner = this
        )
    }
    val circularType =
        SnackProgressBar(SnackProgressBar.TYPE_CIRCULAR, "Loading...")
            .setIsIndeterminate(false)
            .setAllowUserInput(false)
    private lateinit var dereDatabaseHelper: DereDatabaseHelper
    private lateinit var adapter: SongRecyclerViewAdapter

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
    private val publisher: (Int, Int, MusicInfo?, String?) -> Unit =
        { total, progress, info, message ->
            CoroutineScope(Dispatchers.Main).launch {
                circularType.setProgressMax(total)
                if (info != null)
                    adapter.addItem(info)
                snackProgressBarManager.setProgress(progress)
                if (message != null) {
                    circularType.setMessage(message)
                }
                snackProgressBarManager.updateTo(circularType)
            }
        }
    val onFinish: () -> Unit = {
        runOnUiThread {
            adapter.notifyDataSetChanged()
        }
        snackProgressBarManager.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_song_list)

        with(binding) {
            setSupportActionBar(binding.toolbar)
            binding.toolbar.title = title

            binding.fab.setOnClickListener { view ->
                Snackbar.make(view, "What to do?", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            if (twoPane) {
                binding.fab.hide()
            } else {
                binding.fab.show()
            }

            pullToRefresh.setOnRefreshListener {
                pullToRefresh.isRefreshing = false
//            refreshCache(publisher, onFinish)
            }

            tlSongListModes.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    Log.d(TAG, "OnSelectedTab")
//                val tag = tab?.view?.id as? String ?: "normal"
                    val gamemode = GameMode.fromTabIndex(tab?.position ?: 0)
                    Log.d(TAG, "GameMode: $gamemode")
                    refreshMode(gamemode ?: GameMode.NORMAL)
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }

        if (findViewById<FrameLayout>(R.id.song_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }
        adapter = setupRecyclerView(findViewById<RecyclerView>(R.id.song_list))
        snackProgressBarManager.show(circularType, SnackProgressBarManager.LENGTH_INDEFINITE)
        try {
            dereDatabaseHelper = DereDatabaseHelper(this@SongListActivity)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to find database", e)
            Toast.makeText(this, "Failed to find database", Toast.LENGTH_SHORT).show()
            guideInstallDeresute()
            finish()
            return
        }
        DereDatabaseHelper.theInstance = dereDatabaseHelper
        CoroutineScope(Dispatchers.IO).launch {
            if (!dereDatabaseHelper.load(this@SongListActivity, false, publisher, onFinish)) {
                onFailedLoadDatabase()
            }
        }
    }

    private fun refreshMode(gamemode: GameMode) {
        adapter.userFilter.shouldHaveGrand = gamemode == GameMode.GRAND
        adapter.userFilter.shouldHaveMasterPlus = gamemode == GameMode.MASTERPLUS
        adapter.userFilter.shouldHaveSmart = gamemode == GameMode.SMART
        adapter.userFilter.shouldHaveWitch = gamemode == GameMode.WITCH

        adapter.gameMode = gamemode
        findViewById<RecyclerView>(R.id.song_list).adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.filter?.filter("")

    }

    private fun refreshCache(
        publisher: (Int, Int, MusicInfo?, String?) -> Unit,
        onFinish: () -> Unit
    ) {
        snackProgressBarManager.show(circularType, SnackProgressBarManager.LENGTH_INDEFINITE)
        CoroutineScope(Dispatchers.IO).launch {
            if (!dereDatabaseHelper.refreshCache(
                    this@SongListActivity,
                    publisher,
                    onFinish
                )
            ) {
                onFailedLoadDatabase()
            }
            binding.pullToRefresh.isRefreshing = false
        }
    }

    private fun onFailedLoadDatabase() {
        runOnUiThread {
            Snackbar.make(
                binding.mainListLayout,
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        val search_item = menu.findItem(R.id.app_bar_search)
        val searchView: SearchView = search_item.actionView as SearchView
        searchView.isFocusable = false
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                constraint = s
                adapter.filter?.filter(constraint)
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                constraint = s ?: ""
                adapter.filter?.filter(constraint)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.itemId
        when (id) {
            R.id.app_bar_sort -> {
                val sortAlertDialogFragment = SortAlertDialogFragment()
                sortAlertDialogFragment.show(supportFragmentManager, "sortFragment")
            }
            R.id.app_bar_filter -> {
                val filterAlertDialogFragment = FilterAlertDialogFragment(checkedFilters)
                filterAlertDialogFragment.show(supportFragmentManager, "filterFragment")
            }
            R.id.app_bar_refresh -> {
                refreshCache(publisher, onFinish)
            }
            R.id.app_bar_units -> {
                startActivity(Intent(this, UnitListActivity::class.java))
            }
            R.id.export_all_tw -> {
                exportTw()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exportTw() {
        val items = TW5Difficulty.values().map { it.name }.toTypedArray()
        val checked = BooleanArray(items.size)
        AlertDialog.Builder(this).setTitle("Select difficulty")
            .setMultiChoiceItems(items, checked) { dlg, which, check ->
                checked[which] = check
            }.setPositiveButton("OK") { dlg, which ->
                val checkedDifficulties = checked.withIndex().filter { it.value }
                    .map { TW5Difficulty.values()[it.index] }
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/zip"
                    putExtra(Intent.EXTRA_TITLE, "twFiles.zip")

                    // Optionally, specify a URI for the directory that should be opened in
                    // the system file picker before your app creates the document.
                    //                            putExtra(DocumentsContract.EXTRA_INITIAL_URI, null)
                }
                CoroutineScope(Dispatchers.Main).launch {
                    val activityResult = startForResultAsync(intent).await()
                    val code = activityResult.resultCode
                    val data = activityResult.resultData
                    data?.data?.also { uri ->
                        snackProgressBarManager.show(
                            circularType,
                            SnackProgressBarManager.LENGTH_INDEFINITE
                        )
                        withContext(Dispatchers.IO) {
                            try {
                                contentResolver.openFileDescriptor(uri, "w")?.use {
                                    FileOutputStream(it.fileDescriptor).use { fos ->
                                        val list = adapter.getImmutableItemList()
                                        dereDatabaseHelper.exportTW(
                                            list,
                                            checkedDifficulties,
                                            fos
                                        ) { progress, message ->
                                            withContext(Dispatchers.Main) {
                                                circularType.setProgressMax(list.size)
                                                snackProgressBarManager.setProgress(progress)
                                                if (message != null) {
                                                    circularType.setMessage(message)
                                                }
                                                snackProgressBarManager.updateTo(circularType)
                                            }
                                        }
                                    }
                                }
                            } catch (e: FileNotFoundException) {
                                Log.d(TAG, "File not found", e)
                            } catch (e: IOException) {
                                Log.d(TAG, "IOExcpetipon", e)
                            }
                        }
                        snackProgressBarManager.dismiss()
                    }
                }
            }.show()
    }


    private fun setupRecyclerView(recyclerView: RecyclerView): SongRecyclerViewAdapter {
        val adapter = SongRecyclerViewAdapter(this, twoPane)
        adapter.userFilter.addFilter(
            CircleType.All,
            CircleType.Cute,
            CircleType.Cool,
            CircleType.Passion
        )
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        return adapter
    }

    private fun sortList() {
        Toast.makeText(this, "Sort by " + sortType.name, Toast.LENGTH_SHORT).show()
        adapter.sortBy(sortType, sortOrderAsc)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment?, checked: Map<Int, Boolean>) {
        Log.d(TAG, "Permitted:${checked.toList().joinToString()}")
        checkedFilters = HashMap()
        checkedFilters!!.putAll(checked)
        val permittedType: MutableList<CircleType> = ArrayList()
        if (checked[R.id.filterCBTypeAllCheck]!! || checked[R.id.filterCBAllType]!!) {
            permittedType.add(CircleType.All)
        }
        if (checked[R.id.filterCBTypeAllCheck]!! || checked[R.id.filterCBCute]!!) {
            permittedType.add(CircleType.Cute)
        }
        if (checked[R.id.filterCBTypeAllCheck]!! || checked[R.id.filterCBCool]!!) {
            permittedType.add(CircleType.Cool)
        }
        if (checked[R.id.filterCBTypeAllCheck]!! || checked[R.id.filterCBPassion]!!) {
            permittedType.add(CircleType.Passion)
        }
        Log.d(TAG, "Permitted2:${permittedType.toTypedArray().joinToString()}")
        adapter.userFilter.addFilter(*permittedType.toTypedArray())
        adapter.userFilter.shouldHaveMasterPlus = checked[R.id.filterCBMasterPlus] ?: false
        adapter.userFilter.shouldHaveSmart = checked[R.id.filterCBSmart] ?: false
        adapter.userFilter.shouldHaveGrand = checked[R.id.filterCBGrand] ?: false
        adapter.userFilter.shouldBeStarred = checked[R.id.filterCBStarred] ?: false
        adapter.filter?.filter(constraint)
        //sortList()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SortType_KEY, sortType.value)
        outState.putString(Constraint_KEY, constraint)
        if (checkedFilters != null)
            outState.putSerializable(Checked_KEY, checkedFilters as Serializable)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        sortType = SortType.getByValue(savedInstanceState.getInt(SortType_KEY, 0))
        constraint = savedInstanceState.getString(Constraint_KEY, "")
        try {
            checkedFilters =
                savedInstanceState.getSerializable(Checked_KEY) as HashMap<Int, Boolean>?
            if (checkedFilters == null)
                checkedFilters = HashMap()
            if (checkedFilters?.isEmpty() == true) {
                checkedFilters!![R.id.filterCBTypeAllCheck] = true
                checkedFilters!![R.id.filterCBCute] = true
                checkedFilters!![R.id.filterCBCool] = true
                checkedFilters!![R.id.filterCBPassion] = true
                checkedFilters!![R.id.filterCBAllType] = true
                checkedFilters!![R.id.filterCBMasterPlus] = true
            }
            onDialogPositiveClick(null, checkedFilters!!)
            adapter.filter?.filter(constraint)
        } catch (e: Exception) {
            Log.e(TAG, "", e)
        }
    }

    private var sortType: SortType = SortType.Data
    private var sortOrderAsc: Boolean = true
    private var constraint: String = ""
    private var checkedFilters: HashMap<Int, Boolean>? = null
    override fun onDialogPositiveClick(dialog: DialogFragment?, item: Int, ascending: Boolean) {
        sortType = SortType.getByValue(item)
        sortOrderAsc = ascending
        sortList()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {

    }

}

const val SortType_KEY = "SortType"
const val Constraint_KEY = "Constraint"
const val Checked_KEY = "Checked"
