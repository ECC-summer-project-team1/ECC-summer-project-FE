package com.example.toiletfinder

import android.os.Parcel
import android.os.Parcelable

data class ToiletInfo(
    val id: String,
    val startTime: String,
    val endTime: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(startTime)
        parcel.writeString(endTime)
        parcel.writeString(name)
        parcel.writeString(address)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ToiletInfo> {
        override fun createFromParcel(parcel: Parcel): ToiletInfo {
            return ToiletInfo(parcel)
        }

        override fun newArray(size: Int): Array<ToiletInfo?> {
            return arrayOfNulls(size)
        }
    }
}