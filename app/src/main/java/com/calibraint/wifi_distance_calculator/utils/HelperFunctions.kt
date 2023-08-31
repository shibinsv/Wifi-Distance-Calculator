package com.calibraint.wifi_distance_calculator.utils

import android.content.Context
import android.widget.Toast

object HelperFunctions {
    fun showToast(context: Context, message:String?){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}