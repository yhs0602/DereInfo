package com.kyhsgeekcode.dereinfo

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.chrisbanes.photoview.PhotoView
import com.kyhsgeekcode.dereinfo.model.*
import kotlinx.android.synthetic.main.activity_song_detail.*
import kotlinx.android.synthetic.main.song_detail.view.*

/**
 * A fragment representing a single Song detail screen.
 * This fragment is either contained in a [SongListActivity]
 * in two-pane mode (on tablets) or a [SongDetailActivity]
 * on handsets.
 */
class SongDetailFragment : Fragment() {
    val TAG = "SongDetailFrag"

    private var item: MusicInfo? = null
    private var oneMusic: OneMusic? = null
    private var difficulty: TW5Difficulty = TW5Difficulty.Debut
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = DereDatabaseHelper.theInstance.musicIDToInfo[it[ARG_ITEM_ID]]
                activity?.toolbar_layout?.title = item?.name?.replace("\\n", " ")
                activity?.toolbar_layout?.setBackgroundColor(item?.getColor() ?: 0xFFDDDDDD.toInt())
                val musicNumber = DereDatabaseHelper.theInstance.musicIDTomusicNumber[item!!.id]
                Log.w(TAG, "Item.id:${item!!.id}, musicNumber:${musicNumber}")
                //oneMusic = DereDatabaseHelper.theInstance.peekFumens(musicNumber!!)
            }
            if (it.containsKey(ARG_ITEM_DIFFICULTY)) {
                difficulty = it[ARG_ITEM_DIFFICULTY] as TW5Difficulty
                Log.d(TAG, "Contains key, key is:${difficulty.name}")
                //spinnerDifficulty.setSelection(difficulty.ordinal)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.song_detail, container, false)

        // Show the dummy content as text in a TextView.
        item?.let { musicInfo ->
            rootView.song_detail.text = musicInfo.toString()
            val adapter: ArrayAdapter<String> = ArrayAdapter(
                context!!,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.difficulties)
            )
            rootView.spinnerDifficulty.adapter = adapter
            rootView.spinnerDifficulty.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val tw5Difficulty = TW5Difficulty.fromIndex(position)
                        val statistic =
                            DereDatabaseHelper.theInstance.musicInfoIDToStatistic[musicInfo.id]?.get(
                                tw5Difficulty
                            )
                                ?: return
                        rootView.detailedLayout.visibility = View.VISIBLE
                        with(rootView) {
                            buttonStatisticsShowFumen.isEnabled = true
                            buttonStatisticsShowFumen.setOnClickListener {
                                showFumen(context, musicInfo, tw5Difficulty)
                            }

                            val totalCount: Int = statistic[StatisticIndex.Total]?.toInt() ?: 0
                            textViewTotalCount.text =
                                statistic[StatisticIndex.Total]?.formatCleanPercent(2) ?: "-"
                            textViewTotalPercent7.text =
                                statistic[StatisticIndex.Total7]?.formatCleanPercent(2) ?: "-"
                            textViewTotalPercent9.text =
                                statistic[StatisticIndex.Total9]?.formatCleanPercent(2) ?: "-"
                            textViewTotalPercent11.text =
                                statistic[StatisticIndex.Total11]?.formatCleanPercent(2) ?: "-"
                            textViewTotalPercent.text = "100%"

                            textViewNormalCount.text =
                                statistic[StatisticIndex.Normal]?.times(totalCount)?.toInt()
                                    ?.div(100).toString()
                            textViewNormalPercent7.text =
                                statistic[StatisticIndex.Normal7]?.formatCleanPercent(2) ?: "-"
                            textViewNormalPercent9.text =
                                statistic[StatisticIndex.Normal9]?.formatCleanPercent(2) ?: "-"
                            textViewNormalPercent11.text =
                                statistic[StatisticIndex.Normal11]?.formatCleanPercent(2) ?: "-"
                            textViewNormalPercent.text =
                                statistic[StatisticIndex.Normal]?.formatCleanPercent(2) ?: "-"

                            textViewLongCount.text =
                                statistic[StatisticIndex.Long]?.times(totalCount)?.toInt()?.div(100)
                                    .toString()
                            textViewLongPercent7.text =
                                statistic[StatisticIndex.Long7]?.formatCleanPercent(2) ?: "-"
                            textViewLongPercent9.text =
                                statistic[StatisticIndex.Long9]?.formatCleanPercent(2) ?: "-"
                            textViewLongPercent11.text =
                                statistic[StatisticIndex.Long11]?.formatCleanPercent(2) ?: "-"
                            textViewLongPercent.text =
                                statistic[StatisticIndex.Long]?.formatCleanPercent(2) ?: "-"

                            textViewFlickCount.text =
                                statistic[StatisticIndex.Flick]?.times(totalCount)?.toInt()
                                    ?.div(100).toString()
                            textViewFlickPercent7.text =
                                statistic[StatisticIndex.Flick7]?.formatCleanPercent(2) ?: "-"
                            textViewFlickPercent9.text =
                                statistic[StatisticIndex.Flick9]?.formatCleanPercent(2) ?: "-"
                            textViewFlickPercent11.text =
                                statistic[StatisticIndex.Flick11]?.formatCleanPercent(2) ?: "-"
                            textViewFlickPercent.text =
                                statistic[StatisticIndex.Flick]?.formatCleanPercent(2) ?: "-"

                            textViewSlideCount.text =
                                statistic[StatisticIndex.Slide]?.times(totalCount)?.toInt()
                                    ?.div(100).toString()
                            textViewSlidePercent7.text =
                                statistic[StatisticIndex.Slide7]?.formatCleanPercent(2) ?: "-"
                            textViewSlidePercent9.text =
                                statistic[StatisticIndex.Slide9]?.formatCleanPercent(2) ?: "-"
                            textViewSlidePercent11.text =
                                statistic[StatisticIndex.Slide11]?.formatCleanPercent(2) ?: "-"
                            textViewSlidePercent.text =
                                statistic[StatisticIndex.Slide]?.formatCleanPercent(2) ?: "-"

                        }
                    }
                }
            rootView.spinnerDifficulty.setSelection(difficulty.ordinal)
        }
        return rootView
    }

    private fun showFumen(
        context: Context,
        musicInfo: MusicInfo,
        tw5Difficulty: TW5Difficulty
    ) {
        //FumenRenderer(5).render(DereDatabaseHelper.theInstance.parsed)
        val oneDifficulty =
            DereDatabaseHelper.theInstance.parsedFumenCache[Pair(
                musicInfo.id,
                tw5Difficulty
            )]?.difficulties?.get(tw5Difficulty)
        if (oneDifficulty == null) {
            Toast.makeText(
                requireActivity(),
                "Failed to get the Difficulty",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val bitmap =
            FumenRenderer(
                context,
                oneDifficulty.lanes
            ).render(oneDifficulty)
        if (bitmap == null) {
            Toast.makeText(
                requireActivity(),
                "Failed to render",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val photoView = PhotoView(context)
        photoView.setImageBitmap(bitmap)
        val alertDialog = AlertDialog.Builder(context)
            .setTitle("${musicInfo.name} (${oneDifficulty.difficulty})")
            .setView(photoView).show().setOnCancelListener {
                bitmap.recycle()
            }
    }

    private fun shouldEnable(child: Button): Boolean {
        val difficulty =
            oneMusic?.difficulties?.get(TW5Difficulty.fromString(child.text.toString()))
        return difficulty != null
    }

    companion object {
        const val ARG_ITEM_DIFFICULTY = "item_difficulty"

        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
