package com.udacity

import android.app.Application
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.model.DownloadDetail

enum class SelectedRepository {
    RETROFIT,
    GLIDE,
    UDACITY
}

private const val TAG = "MainViewModel"

class MainViewModel(
    private val app: Application
) : AndroidViewModel(app) {
    private val notificationManager: NotificationManager = ContextCompat.getSystemService(
        app,
        NotificationManager::class.java
    ) as NotificationManager

    private var downloadID: Long = 0
    private lateinit var downloadManager: DownloadManager

    private val _isDoanLoadCompleted: MutableLiveData<Boolean> = MutableLiveData(false)
    val isDoanLoadCompleted: LiveData<Boolean> get() = _isDoanLoadCompleted
    private var isSuccess:Boolean = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val query = DownloadManager.Query()
            query.setFilterById(id!!)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()){
                val status=cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when(status){
                    DownloadManager.STATUS_SUCCESSFUL-> {
                        isSuccess= true
                        Log.d(TAG, "onReceive: success isSuccess:$isSuccess")
                    }
                    else-> {
                        isSuccess = false
                        downloadManager.remove(id)
                        Log.d(TAG, "onReceive: failed isSuccess:$isSuccess")
                    }
                }
            }

            if (id == downloadID) {
                _isDoanLoadCompleted.value = true

                sendNotification(
                    app.getString(R.string.notification_title),
                    app.getString(R.string.notification_description)
                )

            } else {
                Toast.makeText(app, "download field", Toast.LENGTH_SHORT).show()
                isSuccess= false
            }
        }
    }


    init {
        app.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


    fun sendNotification(
        title: String,
        description: String
    ) {
        notificationManager.createChannel(
            app.getString(R.string.notification_id),
            app.getString(R.string.chanel_name)
        )
        notificationManager.sendNotification(
            title,
            description,
            app,
            DownloadDetail(SelectedRepository.RETROFIT.name, isSuccess)
        )
    }



    fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(app.getString(R.string.app_name))
                .setDescription(app.getString(R.string.app_description))

                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    app.getString(R.string.app_name) + "." + MimeTypeMap.getFileExtensionFromUrl(
                        URL
                    )
                )
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


        downloadManager =
            app.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }


    fun onDownloadCompleted() {
        _isDoanLoadCompleted.value = false
    }

    fun showToast(selectedRepository: SelectedRepository) {
        Toast.makeText(app, selectedRepository.name, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}