package com.kyhsgeekcode.dereinfo.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.kyhsgeekcode.dereinfo.ExportHelper
import com.kyhsgeekcode.dereinfo.R
import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ExportMusicWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var lastForegroundInfo: ForegroundInfo? = null

    override suspend fun doWork(): Result {
        val inputFolder = inputData.getString(KEY_INPUT_FOLDER) ?: return Result.failure()
        val outputUri = inputData.getString(KEY_OUTPUT_URI)?.let { Uri.parse(it) }
            ?: return Result.failure()

        lastForegroundInfo = createForegroundInfo("Exporting music")

        try {
            setForeground(createForegroundInfo("Exporting music"))
        } catch (e: IllegalStateException) {
            Timber.e(e, "Failed to set foreground")
        }
        val musicFolder = File(inputFolder)
        withContext(Dispatchers.IO) {
            try {
                applicationContext.contentResolver.openFileDescriptor(outputUri, "w")?.use {
                    FileOutputStream(it.fileDescriptor).use { fos ->
                        ExportHelper.exportMusic(
                            context = applicationContext,
                            musicFolder,
                            fos
                        ) { progress, total, message ->
                            try {
                                setForeground(createForegroundInfo("$message ($progress/$total)"))
                            } catch (e: IllegalStateException) {
                                Timber.e(e, "Failed to set foreground")
                            }
                        }
                    }
                }
            } catch (e: FileNotFoundException) {
                Timber.e(e, "File not found")
            } catch (e: IOException) {
                Timber.e(e, "IOExcpetipon")
            }
        }

        return Result.success()
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)
        val cancel = applicationContext.getString(R.string.cancel_export)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(id)
        }

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()
        lastForegroundInfo = ForegroundInfo(1, notification)
        return lastForegroundInfo!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String) {
        // Create a Notification channel
        val name = applicationContext.getString(R.string.notification_channel_id)
        val descriptionText = applicationContext.getString(R.string.notification_title)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(channelId, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(mChannel)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        if (lastForegroundInfo == null) lastForegroundInfo = createForegroundInfo("Exporting music")
        return lastForegroundInfo!!
    }

    companion object {
        const val KEY_OUTPUT_URI = "KEY_OUTPUT_URI"
        const val KEY_INPUT_FOLDER = "KEY_INPUT_FOLDER"
    }
}