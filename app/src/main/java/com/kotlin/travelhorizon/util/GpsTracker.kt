package com.kotlin.travelhorizon.util

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder

class GpsTracker(private val mContext: Context) : Service(), LocationListener {
    private var location: Location? = null
    private var latitude = 0.0
    private var longitude = 0.0

    private var locationManager: LocationManager? = null

    init {
        getLocation()
    }

    // whenever location chanage
    override fun onLocationChanged(p0: Location) {
        latitude = location!!.latitude
        longitude = location!!.longitude
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {
            locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                        this
                    )
                    if (locationManager != null) {
                        location =
                            locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(),
                            this
                        )
                        if (locationManager != null) {
                            location =
                                locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }
        return latitude
    }

    fun getLongtitude(): Double {
        if (location != null) {
            latitude = location!!.longitude
        }
        return longitude
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    fun stopUsingGps(){
        if(locationManager!=null){
            locationManager!!.removeUpdates(this@GpsTracker)
        }
    }

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1
    }
}