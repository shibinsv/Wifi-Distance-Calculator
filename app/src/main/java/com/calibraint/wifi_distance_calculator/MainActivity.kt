package com.calibraint.wifi_distance_calculator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.calibraint.wifi_distance_calculator.databinding.ActivityMainBinding
import com.calibraint.wifi_distance_calculator.utils.HelperFunctions
import com.calibraint.wifi_distance_calculator.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class MainActivity : AppCompatActivity(), LocationListener {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var viewmodel: MainViewmodel
    private lateinit var binding: ActivityMainBinding

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }

            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            }

            else -> {
                HelperFunctions.showToast(this, getString(R.string.no_permission_granted))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewmodel = ViewModelProvider(this)[MainViewmodel::class.java]
        observers()
        initView()
    }

    private fun initView() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        Utils.getWifiInfo(this) { wifiInformation ->
            viewmodel.wifiInformation.postValue(wifiInformation)
        }
    }

    private fun observers() {
        viewmodel.apply {
            binding.apply {
                cvWifi.setOnClickListener {
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS));
                }
                wifiInformation.observe(this@MainActivity) { info ->
                    wifiName.text = info.ssid
                    wifiIp.text = getString(R.string.wifi_ip, info.macAddress)
                    getWifiCoordinates(info.macAddress,
                        onError = { error -> HelperFunctions.showToast(this@MainActivity, error) }
                    )
                }
                location.observe(this@MainActivity) {
                    val distance =
                        "${Utils.getDistanceBetween(location.value, wifiLocation.value)} m"
                    tvDistance.text = getString(R.string.distance, distance)
                }
            }
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            100
        ).build()
        fusedLocationClient?.requestLocationUpdates(
            locationRequest, this, Looper.getMainLooper()
        )
    }


    override fun onLocationChanged(location: Location) {
        viewmodel.location.postValue(
            LocationData(
                location.latitude,
                location.longitude
            )
        )
    }

    override fun onResume() {
        super.onResume()
        fetchLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewmodel.apply {
            location.removeObservers(this@MainActivity)
            wifiInformation.removeObservers(this@MainActivity)
            wifiLocation.removeObservers(this@MainActivity)
        }
    }
}

