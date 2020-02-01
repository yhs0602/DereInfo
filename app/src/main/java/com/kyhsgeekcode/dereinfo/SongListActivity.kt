package com.kyhsgeekcode.dereinfo

import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kyhsgeekcode.dereinfo.model.CircleType.getColor
import com.kyhsgeekcode.dereinfo.model.CircleType.makeRGB
import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import com.kyhsgeekcode.dereinfo.model.MusicInfo
import com.kyhsgeekcode.dereinfo.model.SortType
import com.tingyik90.snackprogressbar.SnackProgressBar
import com.tingyik90.snackprogressbar.SnackProgressBarManager
import kotlinx.android.synthetic.main.activity_song_list.*
import kotlinx.android.synthetic.main.song_list.*
import kotlinx.android.synthetic.main.song_list_content.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [SongDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class SongListActivity : AppCompatActivity(), DialogInterface.OnClickListener {
    val TAG = "SongListActivity"
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
    private lateinit var dereDatabaseHelper: DereDatabaseHelper
    private lateinit var adapter: SongRecyclerViewAdapter
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "What to do?", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val publisher: (Int, Int, MusicInfo?) -> Unit = { total, progress, info ->
            CoroutineScope(Dispatchers.Main).launch {
                val progress100 = progress.toDouble() / total.toDouble() * 100.0
                circularType.setProgressMax(100)
                if (info != null)
                    adapter.addItem(info)
                snackProgressBarManager.setProgress(progress100.toInt())
            }
        }
        val onFinish: () -> Unit = {
            snackProgressBarManager.dismiss()
        }
        pullToRefresh.setOnRefreshListener {
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
                pullToRefresh.isRefreshing = false
            }
        }

        if (song_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }
        adapter = setupRecyclerView(song_list)
        snackProgressBarManager.show(circularType, SnackProgressBarManager.LENGTH_INDEFINITE)
        dereDatabaseHelper = DereDatabaseHelper(this@SongListActivity)
        DereDatabaseHelper.theInstance = dereDatabaseHelper
        CoroutineScope(Dispatchers.IO).launch {
            if (!dereDatabaseHelper.load(this@SongListActivity, false, publisher, onFinish)) {
                onFailedLoadDatabase()
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        val search_item = menu.findItem(R.id.app_bar_search)
        val searchView: SearchView = search_item.actionView as SearchView
        searchView.isFocusable = false
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                adapter.filter?.filter(s)
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                adapter.filter?.filter(s)
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
                val filterAlertDialogFragment = FilterAlertDialogFragment()
                filterAlertDialogFragment.show(supportFragmentManager, "filterFragment")
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setupRecyclerView(recyclerView: RecyclerView): SongRecyclerViewAdapter {
        val adapter = SongRecyclerViewAdapter(this, twoPane)
        recyclerView.adapter = adapter
        return adapter
    }

    class SongRecyclerViewAdapter(
        private
        val parentActivity: SongListActivity,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SongRecyclerViewAdapter.ViewHolder>(), Filterable {

        private var listFilter: ListFilter? = null
        private val onClickListener: View.OnClickListener
        private val values: MutableList<MusicInfo> = ArrayList()
        private var filteredItemList: MutableList<MusicInfo> = values

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as MusicInfo
                if (twoPane) {
                    val fragment = SongDetailFragment().apply {
                        arguments = Bundle().apply {
                            putInt(SongDetailFragment.ARG_ITEM_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.song_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, SongDetailActivity::class.java).apply {
                        putExtra(SongDetailFragment.ARG_ITEM_ID, item.id)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.song_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = filteredItemList[position]
            holder.idView.text = """${item.name}(${item.id})""".replace("\\n", " ")
            holder.contentView.text = item.composer
            holder.backgroundLayout.setBackgroundColor(makeRGB(getColor(item.circleType)))
            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }


        override fun getItemCount() = filteredItemList.size

        fun addItem(item: MusicInfo) {
            values.add(item)
            notifyDataSetChanged()
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
            val backgroundLayout: LinearLayout = view.listitem_background
        }

        override fun getFilter(): Filter? {
            if (listFilter == null) {
                listFilter = ListFilter()
            }
            return listFilter
        }

        inner class ListFilter : Filter() {
            val TAG = "ListFilter"
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val TAG = "ListFilter"
                Log.d(TAG, "Filter called$constraint")
                val results = FilterResults()
                if (constraint == null || constraint.isEmpty()) {
                    results.values = values
                    results.count = values.size
                } else {
                    val itemList: ArrayList<MusicInfo> = ArrayList()
                    for (item in values) {
                        val name = """${item.name}(${item.id})"""
                        if (name.toUpperCase().contains(constraint.toString().toUpperCase())) {
                            itemList.add(item)
                        }
                    }
                    results.values = itemList
                    results.count = itemList.size
                }
                return results
            }

            override fun publishResults(
                constraint: CharSequence,
                results: FilterResults
            ) { // update listview by filtered data list.
                filteredItemList = results.values as ArrayList<MusicInfo>
                Log.d(TAG, """filtered:${filteredItemList.size}, original:${values.size}""")

                // notify
                if (results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        val sortType = SortType.getByValue(which)
        sortList(sortType ?: SortType.Data)
    }

    private fun sortList(sortType: SortType) {
        Toast.makeText(this, "Sort by " + sortType.name, Toast.LENGTH_SHORT).show()
    }
}
