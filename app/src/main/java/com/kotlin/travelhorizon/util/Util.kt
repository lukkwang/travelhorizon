package com.kotlin.travelhorizon.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

class Util {
    companion object {
        fun openMapApp(context: Context, latitude: String, longitude: String) {
            val uri = Uri.parse("geo:${latitude},${longitude}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(context, intent, null)
        }
    }
}