package com.calibraint.wifi_distance_calculator

import android.net.wifi.WifiInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.calibraint.wifi_distance_calculator.network.ApiCall


class MainViewmodel : ViewModel() {
    var wifiInformation = MutableLiveData<WifiInfo>()
    var wifiLocation = MutableLiveData<LocationData>()
    var location = MutableLiveData<LocationData>()

    fun getWifiCoordinates(macAddress: String, onError: (String) -> Unit) {
        ApiCall.getWifiInfo(macAddress, onSuccess = { data ->
            wifiLocation.postValue(
                LocationData(
                    data?.location?.latitude ?: 0.0,
                    data?.location?.longitude ?: 0.0
                )
            )
        }, onError)
    }
}

data class LocationData(
    var latitide: Double,
    var longitude: Double,
)