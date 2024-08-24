package com.example.toiletfinder

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.vectormap.LatLng

class LocationViewModel : ViewModel() {
    private val _location = MutableLiveData<LatLng>()
    val location: LiveData<LatLng> get() = _location
    val toiletList: MutableLiveData<List<ToiletInfo>> = MutableLiveData()

    private val _moveCameraToFirstToilet = MutableLiveData<Boolean>()
    val moveCameraToFirstToilet: LiveData<Boolean> get() = _moveCameraToFirstToilet

    fun updateLocation(latLng: LatLng) {
        _location.postValue(latLng)
    }

    fun updateToiletList(toilets: List<ToiletInfo>) {
        toiletList.postValue(toilets)
    }

    fun requestMoveCameraToFirstToilet() {
        _moveCameraToFirstToilet.postValue(true)
    }

    fun cameraMoved() {
        _moveCameraToFirstToilet.postValue(false)
    }
}