package com.kyhsgeekcode.dereinfo.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kyhsgeekcode.dereinfo.model.DereDatabaseService
import com.kyhsgeekcode.dereinfo.model.MusicInfo
import com.kyhsgeekcode.dereinfo.model.TW5Difficulty
import com.kyhsgeekcode.dereinfo.viewmodel.FumenViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fumen_fragment.view.*
import timber.log.Timber

@AndroidEntryPoint
class FumenFragment : Fragment() {
    companion object {
        fun newInstance(musicInfo: MusicInfo, tW5Difficulty: TW5Difficulty) =
            FumenFragment().apply {
                val args = Bundle()
                args.putSerializable("musicInfo", musicInfo)
                args.putSerializable("tw5Difficulty", tW5Difficulty)
                arguments = args
            }
    }

    private val viewModel: FumenViewModel by viewModels()

    private var bitmap: Bitmap? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fumen_fragment, container, false)
        val context = requireContext()
        val musicInfo: MusicInfo
        val tw5Difficulty: TW5Difficulty
        requireArguments().let {
            musicInfo = it.getSerializable("musicInfo") as MusicInfo
            tw5Difficulty = it.getSerializable("tw5Difficulty") as TW5Difficulty
            Timber.d("musicInfo:$musicInfo, tw5Difficulty:$tw5Difficulty")
        }
        //FumenRenderer(5).render(DereDatabaseHelper.theInstance.parsed)
        val oneDifficulty =
            DereDatabaseService.theInstance.parsedFumenCache[Pair(
                musicInfo.id,
                tw5Difficulty
            )]?.difficulties?.get(tw5Difficulty)
        if (oneDifficulty == null) {
            Toast.makeText(
                requireActivity(),
                "Failed to get the Difficulty",
                Toast.LENGTH_SHORT
            ).show()
//            parentFragmentManager.popBackStackImmediate()
            return root
        }
        bitmap =
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
            return root
        }
        Timber.d("Bitmap: $bitmap")
        root.pv_fumen.setImageBitmap(bitmap)
//        saveImage(bitmap!!, requireContext(), "dereinfo")
        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        bitmap?.recycle()
    }
}