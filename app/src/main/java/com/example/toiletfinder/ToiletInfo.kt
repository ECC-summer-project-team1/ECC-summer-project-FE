package com.example.toiletfinder

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class ToiletInfo(
    val category: String = "",
    val reference: String = "",
    val toiletName: String = "",
    val addressRoad: String = "",
    val addressJibun: String = "",
    val maleToiletCount: Int = 0,
    val maleChildToiletCount: Int = 0,
    val maleDisabledToiletCount: Int = 0,
    val maleChildDisabledToiletCount: Int = 0,
    val femaleToiletCount: Int = 0,
    val femaleChildToiletCount: Int = 0,
    val femaleDisabledToiletCount: Int = 0,
    val femaleChildDisabledToiletCount: Int = 0,
    val managingInstitution: String = "",
    val managingPhoneNumber: String = "",
    val openingHours: String = "",
    val detailedOpeningHours: String = "",
    val installationDate: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val ownershipType: String = "",
    val wasteManagementType: String = "",
    val safetyFacilityRequired: String = "",
    val emergencyBell: String = "",
    val emergencyCctv: String = "",
    val diaperChangingStation: String = "",
    val diaperChangingStationLocation: String = "",
    val remodelingDate: String = "",
    val dataStandardDate: String = ""
) : Serializable