package com.kyhsgeekcode.dereinfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kyhsgeekcode.dereinfo.model.CircleType
import com.kyhsgeekcode.dereinfo.model.MusicInfo
import com.kyhsgeekcode.dereinfo.model.SongFilter
import com.kyhsgeekcode.dereinfo.model.SortType
import com.wanakanajava.WanaKanaJava
import kotlinx.android.synthetic.main.song_list_content.view.*
import net.crizin.KoreanRomanizer

class SongRecyclerViewAdapter(
    private
    val parentActivity: SongListActivity,
    private val twoPane: Boolean
) :
    RecyclerView.Adapter<SongRecyclerViewAdapter.ViewHolder>(),
    Filterable {

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
                val intent = Intent(
                    v.context,
                    SongDetailActivity::class.java
                ).apply {
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
        holder.backgroundLayout.setBackgroundColor(
            CircleType.makeRGB(
                CircleType.getColor(item.circleType)
            )
        )
        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }


    override fun getItemCount() = filteredItemList.size

    fun clear() {
        values.clear()
        filteredItemList.clear()
    }

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
            Log.d(TAG, "Filter called$constraint")
            val results = FilterResults()
            val constraintStringUpper = constraint.toString().toUpperCase()
            val romanizedConstraint = KoreanRomanizer.romanize(
                constraintStringUpper
            ).toUpperCase()
            Log.d(TAG, "Romanized:${romanizedConstraint}")
            if (constraint == null) {
                results.values = values
                results.count = values.size
            } else {
                val itemList: ArrayList<MusicInfo> = ArrayList()
                for (item in values) {
                    val name = """${item.name}(${item.id})""".toUpperCase()
                    val nameKanaUpper = item.nameKana.toUpperCase()
                    val romanjiNameUpper = WanaKanaJava.toRomaji(
                        nameKanaUpper
                    )
                        ?.toUpperCase()
                    Log.d(TAG, "Filtering")
                    if ((name.contains(constraintStringUpper) ||
                                nameKanaUpper.contains(constraintStringUpper) ||
                                romanjiNameUpper?.contains(constraintStringUpper) == true ||
                                romanjiNameUpper?.contains(romanizedConstraint) == true ||
                                name.contains(romanizedConstraint)) &&
                        userFilter.pass(item)
                    ) {
                        itemList.add(item)
                    }
                }
                itemList.sortBy{
                    sortType.condition(it) as Comparable<Any>
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
            Log.d(
                TAG,
                """filtered:${filteredItemList.size}, original:${values.size}"""
            )

            // notify
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetChanged()
            }
        }
    }

    fun sortBy(sortType: SortType) {
        this.sortType = sortType
        filteredItemList.sortBy {
            sortType.condition(it) as Comparable<Any>
        }
        notifyDataSetChanged()
    }

    var userFilter: SongFilter = SongFilter()
    var sortType : SortType = SortType.Alphabetical
}