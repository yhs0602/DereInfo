package com.kyhsgeekcode.dereinfo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kyhsgeekcode.dereinfo.dummy.DummyContent
import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import com.kyhsgeekcode.dereinfo.model.MusicInfo
import com.kyhsgeekcode.dereinfo.model.getColor
import com.kyhsgeekcode.dereinfo.model.makeRGB
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
class SongListActivity : AppCompatActivity() {
    private val snackProgressBarManager by lazy {
        SnackProgressBarManager(
            mainListLayout,
            lifecycleOwner = this
        )
    }
    val circularType =
        SnackProgressBar(SnackProgressBar.TYPE_CIRCULAR, "Loading...")
            .setIsIndeterminate(false)
            .setAllowUserInput(true)
    private lateinit var dereDatabaseHelper: DereDatabaseHelper
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

        if (song_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }
        val adapter = setupRecyclerView(song_list)
        snackProgressBarManager.show(circularType, SnackProgressBarManager.LENGTH_INDEFINITE)
        CoroutineScope(Dispatchers.IO).launch {
            dereDatabaseHelper = DereDatabaseHelper(this@SongListActivity)
            dereDatabaseHelper.parseDatabases({ current, total, musicInfo ->
                CoroutineScope(Dispatchers.Main).launch {
                    adapter.addItem(musicInfo)
                }
                circularType.setProgressMax(total)
                CoroutineScope(Dispatchers.Main).launch {
                    snackProgressBarManager.setProgress(current)
                }
            }) {
                snackProgressBarManager.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        snackProgressBarManager.disable()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView): SongRecyclerViewAdapter {
        val adapter = SongRecyclerViewAdapter(this, twoPane)
        recyclerView.adapter = adapter
        return adapter
    }

    class SongRecyclerViewAdapter(
        private val parentActivity: SongListActivity,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SongRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener
        private val values: MutableList<MusicInfo> = ArrayList()

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as MusicInfo
                if (twoPane) {
                    val fragment = SongDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(SongDetailFragment.ARG_ITEM_ID, item.name)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.song_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, SongDetailActivity::class.java).apply {
                        putExtra(SongDetailFragment.ARG_ITEM_ID, item.name)
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
            val item = values[position]
            holder.idView.text = item.name.replace("\\n"," ")
            holder.contentView.text = item.composer
            holder.backgroundLayout.setBackgroundColor(makeRGB(getColor(item.circleType)))
            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }


        override fun getItemCount() = values.size

        fun addItem(item: MusicInfo) {
            values.add(item)
            notifyDataSetChanged()
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.id_text
            val contentView: TextView = view.content
            val backgroundLayout : LinearLayout = view.listitem_background
        }
    }
}
