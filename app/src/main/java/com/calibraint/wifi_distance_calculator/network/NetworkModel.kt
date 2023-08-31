package com.calibraint.wifi_distance_calculator.network

import com.google.gson.annotations.SerializedName

data class WifiModel(
    var location: LocationModel? = LocationModel(),
    var accuracy: Double? = null,
)

data class LocationModel(
    @SerializedName("lat") var latitude: Double? = null,
    @SerializedName("lng") var longitude: Double? = null,
)

data class WifiRequest(
    @SerializedName("wifiAccessPoints") var wifiAccessPoints: ArrayList<WifiAccessPoints> = arrayListOf(),
)

data class WifiAccessPoints(
    @SerializedName("macAddress") var macAddress: String? = null,
)