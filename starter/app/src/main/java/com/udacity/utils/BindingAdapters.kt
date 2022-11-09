package com.udacity.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.udacity.R

@BindingAdapter("setDownloadStatus")
fun setDownloadStatus(textView: TextView, isSuccess: Boolean) {
    if (isSuccess) {
        textView.text = textView.context.getString(R.string.success_state)
        textView.setTextColor(textView.context.getColor(R.color.green))
    } else {
        textView.text = textView.context.getString(R.string.fail_state)
        textView.setTextColor(textView.context.getColor(R.color.read))
    }
}