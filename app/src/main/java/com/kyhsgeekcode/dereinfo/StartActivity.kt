package com.kyhsgeekcode.dereinfo

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gun0912.tedpermission.coroutine.TedPermission
import com.kyhsgeekcode.dereinfo.databinding.ActivityStartBinding
import kotlinx.coroutines.launch

//asks permission, and go to main activity
class StartActivity : AppCompatActivity() {
//    val MY_PERMISSIONS_REQUEST_READ_STORAGE = 1234
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        lifecycleScope.launch {
            do {
                val permissionResult =
                    TedPermission.create()
                        .setPermissions(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        .check()
            } while (permissionResult.isGranted)
            launchActivity(this@StartActivity, SongListActivity::class.java)
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            MY_PERMISSIONS_REQUEST_READ_STORAGE -> {
//                // If request is cancelled, the result arrays are empty.
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    launchActivity(this, SongListActivity::class.java)
//                } else {
//                    Snackbar.make(
//                        binding.startDefault,
//                        "This app requires the permission to operate",
//                        Snackbar.LENGTH_LONG
//                    )
//                        .setAction("Ask permission again") {
//                            requestPermission()
//                        }.show()
//                }
//                return
//            }
//
//            // Add other 'when' lines to check for other
//            // permissions this app might request.
//            else -> {
//                // Ignore all other requests.
//            }
//        }
//    }
//
//    private fun requestPermission(): Boolean {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            )
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Permission is not granted
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this,
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//            ) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    MY_PERMISSIONS_REQUEST_READ_STORAGE
//                )
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//            return false
//        } else {
//            // Permission has already been granted
//            return true
//        }
//    }
}
