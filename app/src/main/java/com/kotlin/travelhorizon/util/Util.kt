package com.kotlin.travelhorizon.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.kotlin.travelhorizon.R

class Util {
    companion object {

        /**
         * retrieve GPS Location
         * @return key: latitude,  longitude
         */
        fun getLocation(context: Context): Map<String, String> {

            val locationMap: MutableMap<String, String> = mutableMapOf<String, String>()

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                location?.let {
                    locationMap.put("latitude", location.latitude.toString())
                    locationMap.put("longitude", location.longitude.toString())
//                    val latitude = location.latitude
//                    val longitude = location.longitude
//                    val accuracy = location.accuracy
//                    val time = location.time
//                    Log.d("GPS Location", "latitude: latitude: $latitude, location: $location, accuracy: $accuracy, time: $time")
                }
            } else {
                Toast.makeText(context,
                    context.getResources().getString(R.string.no_gps_permission),
                    Toast.LENGTH_SHORT
                ).show();
            }

            return locationMap
        }


        fun openMapApp(context: Context, latitude: String, longitude: String) {
            val uri = Uri.parse("geo:${latitude},${longitude}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(context, intent, null)
        }
    }
}