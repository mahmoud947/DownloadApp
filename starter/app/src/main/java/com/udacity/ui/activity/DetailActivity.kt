package com.udacity.ui.activity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.FILE_NAME_EXTRA
import com.udacity.R
import com.udacity.STATUS_EXTRA
import com.udacity.clearAllNotification
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.model.DownloadDetail


class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setSupportActionBar(binding.toolbar)
        val downloadDetail = DownloadDetail(
            intent.getStringExtra(FILE_NAME_EXTRA) ?: getString(R.string.un_known),
            intent.getBooleanExtra(
                STATUS_EXTRA, false
            )
        )
        binding.layout.lifecycleOwner = this
        binding.layout.detail = downloadDetail
        binding.layout.button.setOnClickListener {

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val notificationManager: NotificationManager =
            this.getSystemService(NotificationManager::class.java)
        notificationManager.clearAllNotification()
    }

}
