package com.udacity.view_models

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
import com.udacity.R
import com.udacity.createChannel
import com.udacity.model.DownloadDetail
import com.udacity.sendNotification
import com.udacity.ui.custom_view.ButtonState

sealed class SelectedRepository(url:String?) {
    data class Retrofit(val url: String): SelectedRepository(url = url)
    data class Glide(val url: String): SelectedRepository(url = url)
    data class Udacity(val url: String): SelectedRepository(url = url)
    object Empty: SelectedRepository(null)
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

    private var isSuccess: Boolean = false
    private var fileName:String = ""

    private val _buttonState: MutableLiveData<ButtonState> = MutableLiveData(ButtonState.Completed)
    val buttonState: LiveData<ButtonState> get() = _buttonState

    private val _url:MutableLiveData<SelectedRepository?> = MutableLiveData(null)




    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val query = DownloadManager.Query()
            query.setFilterById(id!!)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        isSuccess = true
                        _buttonState.value = ButtonState.Completed
                        Log.d(TAG, "onReceive: success isSuccess:$isSuccess")
                        sendNotification(
                            app.getString(R.string.notification_title),
                            "$fileName is Downloaded",
                            fileName
                        )
                    }
                    else -> {
                        isSuccess = false
                        _buttonState.value = ButtonState.Completed
                        Log.d(TAG, "onReceive: failed isSuccess:$isSuccess")
                        sendNotification(
                            app.getString(R.string.notification_title),
                            "$fileName is Download field",
                            fileName
                        )
                    }
                }
                if (id!=downloadID){
                    isSuccess = false
                    _buttonState.value = ButtonState.Completed
                }
            }

        }
    }


    init {
        app.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }


    fun sendNotification(
        title: String,
        description: String,
        fileName:String
    ) {
        notificationManager.createChannel(
            app.getString(R.string.notification_id),
            app.getString(R.string.chanel_name)
        )
        notificationManager.sendNotification(
            title,
            description,
            app,
            DownloadDetail(fileName, isSuccess)
        )
    }

    fun download(){
        val url = _url.value
        when(url){
            is SelectedRepository.Udacity ->{
                fileName = app.getString(R.string.loadapp_current_repository_by_udacity)
                handelDownload(url.url)
            }
            is SelectedRepository.Retrofit ->{
                fileName = app.getString(R.string.retorfit_type_save_http_client_for_android_and_java_by_square_inc)
                handelDownload(url.url)
            }
            is SelectedRepository.Glide ->{
                fileName = app.getString(R.string.glide_image_loading_library_by_bump_tech)
                handelDownload(url.url)
            }
            else -> {
                Toast.makeText(app,"Select any thing",Toast.LENGTH_SHORT).show()
                _buttonState.value = ButtonState.Clicked
            }
        }

    }

    private fun handelDownload(url: String) {

        _buttonState.value = ButtonState.Loading
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription(app.getString(R.string.app_description))

                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    app.getString(R.string.app_name) + "." + MimeTypeMap.getFileExtensionFromUrl(
                        url
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
    fun setUrl(url: SelectedRepository){
        _url.value = url
    }


}