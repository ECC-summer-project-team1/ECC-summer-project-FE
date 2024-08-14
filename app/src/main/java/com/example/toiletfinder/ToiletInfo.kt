package com.example.toiletfinder

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class ToiletInfo(
    val toiletName: String,
    val addressRoad: String,
    val phoneNumber: String,
    val managingInstitution : String,
    val openingHours: String,
) : Serializable