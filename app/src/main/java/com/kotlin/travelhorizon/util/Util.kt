package com.kotlin.travelhorizon.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

class Util {
    companion object {

        const val URL_APP_DB = "/data/data/com.kotlin.travelhorizon/databases/travel_horizon"

        fun openMapApp(context: Context, latitude: String, longitude: String) {
            val uri = Uri.parse("geo:${latitude},${longitude}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(context, intent, null)
        }


        fun getAppVersionName(context: Context): String {
            try {
                val packageName = context.packageName // 또는 requireContext().packageName
                val packageManager = context.packageManager

                val packageInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    packageManager.getPackageInfo(packageName, android.content.pm.PackageManager.PackageInfoFlags.of(0))
                } else {
                    // Deprecated for newer APIs, but works for older ones
                    @Suppress("DEPRECATION")
                    packageManager.getPackageInfo(packageName, 0)
                }

                return packageInfo.versionName ?: "0"

            } catch (e: Exception) {
                e.printStackTrace()
                return "Unknown" // 버전 정보를 가져오는데 실패했을 때 반환할 기본값
            }
        }


        /**
         * retrieve GPS Location
         * @return key: latitude,  longitude
         */
        /*fun getLocation(context: Context): Map<String, String> {

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
        }*/
    }
}