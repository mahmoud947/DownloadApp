package com.udacity.ui.activity

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.udacity.ui.custom_view.ButtonState
import com.udacity.MainViewModel
import com.udacity.R
import com.udacity.SelectedRepository
import com.udacity.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var viewModel: MainViewModel


    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]



        binding.layout.customButton.setOnClickListener {
            binding.layout.customButton.setButtonState(ButtonState.Loading)
            viewModel.download()
        }


        viewModel.isDoanLoadCompleted.observe(this, Observer {
            if (it){
                binding.layout.customButton.setButtonState(ButtonState.Completed)
                viewModel.onDownloadCompleted()
            }
        })

        binding.layout.radioGroup.setOnCheckedChangeListener { radioGroup, id ->
            when(id){
                binding.layout.glideRadioBtn.id->{
                   viewModel.showToast(SelectedRepository.GLIDE)
                }
                binding.layout.udacityRadioBtn.id->{
                    viewModel.showToast(SelectedRepository.UDACITY)
                }
                binding.layout.retrofitRadioBtn.id->{
                    viewModel.showToast(SelectedRepository.RETROFIT)
                }
            }

        }


    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            0
        )

    }

    private fun isPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }



}
