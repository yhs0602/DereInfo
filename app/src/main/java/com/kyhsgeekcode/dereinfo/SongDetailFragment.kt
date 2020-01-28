package com.kyhsgeekcode.dereinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import com.kyhsgeekcode.dereinfo.model.MusicInfo
import kotlinx.android.synthetic.main.activity_song_detail.*
import kotlinx.android.synthetic.main.song_detail.view.*

/**
 * A fragment representing a single Song detail screen.
 * This fragment is either contained in a [SongListActivity]
 * in two-pane mode (on tablets) or a [SongDetailActivity]
 * on handsets.
 */
class SongDetailFragment : Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var item: MusicInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = DereDatabaseHelper.theInstance.musicIDToInfo[it[ARG_ITEM_ID]]
                activity?.toolbar_layout?.title = item?.name?.replace("\\n", " ")
                activity?.toolbar_layout?.setBackgroundColor(item?.getColor()?: 0xFFDDDDDD.toInt())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.song_detail, container, false)

        // Show the dummy content as text in a TextView.
        item?.let {
            rootView.song_detail.text = it.toString()
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
