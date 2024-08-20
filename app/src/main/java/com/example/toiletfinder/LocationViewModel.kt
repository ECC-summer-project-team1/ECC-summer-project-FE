package com.example.toiletfinder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.vectormap.LatLng

class LocationViewModel : ViewModel() {
    private val _location = MutableLiveData<LatLng>()
    val location: LiveData<LatLng> get() = _location

    fun updateLocation(latLng: LatLng) {
        _location.postValue(latLng)
    }
}