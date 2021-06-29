package com.kyhsgeekcode.dereinfo

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.github.chrisbanes.photoview.PhotoView
import com.kyhsgeekcode.dereinfo.model.*
import kotlinx.android.synthetic.main.activity_song_detail.*
import kotlinx.android.synthetic.main.song_detail.view.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


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
    private var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
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
                        difficulty = TW5Difficulty.fromIndex(position)
                        val statistic =
                            DereDatabaseHelper.theInstance.musicInfoIDToStatistic[musicInfo.id]?.get(
                                difficulty
                            )
                                ?: return
                        rootView.detailedLayout.visibility = View.VISIBLE
                        with(rootView) {
//                            buttonStatisticsShowFumen.isEnabled = true
//                            buttonStatisticsShowFumen.setOnClickListener {
////                                showFumen(musicInfo, tw5Difficulty)
//                                showFumen2(musicInfo, tw5Difficulty)
//                            }

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


                            bitmap = createFumenBitmap(musicInfo, difficulty)
                            bitmap?.let { bm ->
                                iv_song_detail.setImageBitmap(bm)
                                val lp = iv_song_detail.layoutParams
                                lp.height = bm.height / bm.width * iv_song_detail.width
                                iv_song_detail.layoutParams = lp

                                iv_song_detail.setOnClickListener {
                                    val photoView = PhotoView(context)
                                    photoView.setImageBitmap(bitmap)
                                    val alertDialog = AlertDialog.Builder(context)
                                        .setTitle("${musicInfo.name} (${difficulty})")
                                        .setView(photoView).show()
//                                        .setOnCancelListener {
////                                            bm.recycle()
//                                        }
                                }

                            }

                        }
                    }
                }
            rootView.spinnerDifficulty.setSelection(difficulty.ordinal)
        }
        return rootView
    }

    private fun showFumen2(
        musicInfo: MusicInfo,
        tw5Difficulty: TW5Difficulty
    ) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.song_detail_container,
                FumenFragment.newInstance(musicInfo, tw5Difficulty)
            ).addToBackStack(null)
            .commit()
    }


    private fun createFumenBitmap(musicInfo: MusicInfo, tw5Difficulty: TW5Difficulty): Bitmap? {
        val context = requireContext()
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
            return null
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
            return null
        }
        return bitmap
    }

    private fun showFumen(
        musicInfo: MusicInfo,
        tw5Difficulty: TW5Difficulty
    ) {
        val context = requireContext()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.detail_menu, menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_detail_export_image) {
            if (bitmap == null) {
                Toast.makeText(requireActivity(), "No image", Toast.LENGTH_SHORT).show()
                return true
            }
            saveImage(bitmap!!, requireContext(), "dereinfo")
            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()
            return true
        } else if (item.itemId == R.id.action_detail_export_db) {
            if (this.item == null) {
                Toast.makeText(requireActivity(), "No Item", Toast.LENGTH_SHORT).show()

            } else {
                Log.d(TAG, "id:${this.item?.id}")
                val file =
                    DereDatabaseHelper.theInstance.musicNumberToFumenFile[DereDatabaseHelper.theInstance.musicIDTomusicNumber[this.item?.id]]
                Log.d(TAG, "Size=${DereDatabaseHelper.theInstance.musicNumberToFumenFile.size}")
                if (file == null) {
                    Toast.makeText(requireActivity(), "No db file", Toast.LENGTH_SHORT).show()
                } else {
                    shareAsZip(file, "Share as db file")
                }
            }
        } else if (item.itemId == R.id.action_detail_export_tw) {
            if (this.item == null) {

            } else {
                val oneDifficulty =
                    DereDatabaseHelper.theInstance.parsedFumenCache[Pair(
                        this.item!!.id,
                        difficulty
                    )]?.difficulties?.get(difficulty)
                val json = oneDifficulty!!.toJson(this.item!!)
                val fileName = "${this.item?.name}-${difficulty.name}___"
                val temp = File.createTempFile(fileName, ".txt", context!!.cacheDir)
                temp.printWriter().use {
                    it.print(json)
                }
                shareFile(temp, "text/plain", "Share as tw file")
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun shareAsZip(file: File, message: String) {
        val temp =
            File.createTempFile(this.item?.name ?: "temp", ".zip", context!!.cacheDir)
        val out = ZipOutputStream(BufferedOutputStream(FileOutputStream(temp)))
        val entry = ZipEntry(file.name)
        out.putNextEntry(entry)
        file.inputStream().copyTo(out)
        out.close()
        shareFile(temp, "application/zip", message)
    }

    private fun shareFile(file: File, type: String, message: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = type
        val contentUri: Uri = FileProvider.getUriForFile(
            requireContext().applicationContext,
            requireContext().packageName.toString() + ".fileprovider",
            file
        )
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        startActivity(Intent.createChooser(shareIntent, message))
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
