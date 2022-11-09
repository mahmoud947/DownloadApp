package com.udacity.utils

import android.widget.TextView
import com.udacity.R


fun setDownloadStatus(textView: TextView, isSuccess: Boolean) {
    if (isSuccess) {
        textView.text = textView.context.getString(R.string.success_state)
    } else {
        textView.text = textView.context.getString(R.string.fail_state)
    }
}