package com.calibraint.wifi_distance_calculator.utils

import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.calibraint.wifi_distance_calculator.LocationData

object Utils {

    fun getWifiInfo(context: Context, wifiInformation: (WifiInfo) -> Unit) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val request = NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build()
                val networkCallback =
                    object : ConnectivityManager.NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {
                        override fun onCapabilitiesChanged(
                            network: Network,
                            networkCapabilities: NetworkCapabilities,
                        ) {
                            super.onCapabilitiesChanged(network, networkCapabilities)
                            val info = networkCapabilities.transportInfo as WifiInfo
                            wifiInformation(info)

                        }
                    }
                connectivityManager.registerNetworkCallback(request, networkCallback)
            } else {
                wifiInformation(wifiManager.connectionInfo)
            }
        } catch (e: Exception) {
            wifiInformation(wifiManager.connectionInfo)
        }
    }

    fun getDistanceBetween(mobileLocation: LocationData?, wifiLocation: LocationData?): Float {
        val locationA = Location("locationA")
        locationA.latitude = mobileLocation?.latitide ?: 0.0
        locationA.longitude = mobileLocation?.longitude ?: 0.0
        val locationB = Location("locationB")
        locationB.latitude = wifiLocation?.latitide ?: 0.0
        locationB.longitude = wifiLocation?.longitude ?: 0.0
        val distance = locationA.distanceTo(locationB)
        return distance
    }

}