package com.kyhsgeekcode.dereinfo

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.kyhsgeekcode.dereinfo.databinding.ActivitySongDetailBinding

/**
 * An activity representing a single Song detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [SongListActivity].
 */
class SongDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.detailToolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = SongDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(
                        SongDetailFragment.ARG_ITEM_ID,
                        intent.getIntExtra(SongDetailFragment.ARG_ITEM_ID, -1)
                    )
                    putSerializable(
                        SongDetailFragment.ARG_ITEM_DIFFICULTY,
                        intent.getSerializableExtra(SongDetailFragment.ARG_ITEM_DIFFICULTY)
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.song_detail_container, fragment)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                navigateUpTo(Intent(this, SongListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
