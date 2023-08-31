package com.calibraint.wifi_distance_calculator.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ApiCall {

    companion object {
        private val baseUrl = "https://www.googleapis.com/geolocation/"
        private val apiKey = "AIzaSyAOusMYvePlAqv-LilkF43bM_Q_Peu2hmk"
        val apiService =
            RetrofitClient.getClient(baseUrl).create(ApiService::class.java)

        fun getWifiInfo(
            macAddress: String,
            onSuccess: (WifiModel?) -> Unit,
            onError: (String) -> Unit,
        ) {
            apiService.getWifiLocation(
                apiKey,
                WifiRequest(wifiAccessPoints = arrayListOf(WifiAccessPoints(macAddress)))
            ).enqueue(
                object : Callback<WifiModel> {
                    override fun onResponse(call: Call<WifiModel>, response: Response<WifiModel>) {
                        if (response.isSuccessful) {
                            onSuccess(response.body())
                        } else {
                            onError(response.errorBody().toString())
                        }
                    }

                    override fun onFailure(call: Call<WifiModel>, t: Throwable) {
                        onError(t.message.toString())
                    }
                }
            )
        }
    }
}