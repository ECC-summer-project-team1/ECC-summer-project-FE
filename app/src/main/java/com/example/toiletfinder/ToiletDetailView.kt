package com.example.toiletfinder

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kakao.vectormap.MapView
import java.io.Serializable


class ToiletDetailView : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var toiletInfo: ToiletInfo
    private var startLocation: Pair<Double, Double>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toilet_detail_view)

        fun <T : Serializable> Intent.intentSerializable(key: String, clazz: Class<T>): T? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.getSerializableExtra(key, clazz)
            } else {
                this.getSerializableExtra(key) as T?
            }
        }

        // Retrieve ToiletInfo from Intent
        toiletInfo = intent.intentSerializable("toilet_info", ToiletInfo::class.java) ?: return

        val currentLat = intent.getDoubleExtra("current_latitude", 0.0)
        val currentLng = intent.getDoubleExtra("current_longitude", 0.0)
        startLocation = Pair(currentLat, currentLng)


        // Initialize MapView
        mapView = findViewById(R.id.detail_map_view)
        if (toiletInfo != null) {
            MapViewHelper.initializeMapView(mapView, toiletInfo.latitude, toiletInfo.longitude) {
                // Additional map settings
            }

            // Populate UI with ToiletInfo details
            val textViewRestroom: TextView = findViewById(R.id.textViewRestroom)
            val textViewHours: TextView = findViewById(R.id.textViewHours)
            val textViewAddress: TextView = findViewById(R.id.textViewAddress)
            val textViewPhoneNumber: TextView = findViewById(R.id.textViewPhoneNumber)
            val textViewManagingInstitution: TextView = findViewById(R.id.textViewManagingInstitution)

            textViewRestroom.text = toiletInfo.toiletName
            textViewHours.text = "운영시간: ${toiletInfo.openingHours}"
            textViewAddress.text = "주소: ${toiletInfo.addressRoad}"
            textViewPhoneNumber.text = "전화번호: ${toiletInfo.managingPhoneNumber}"
            textViewManagingInstitution.text = "관리 기관: ${toiletInfo.managingInstitution}"
        }

        // Back button setup
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val backButton: TextView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val mapButton: ImageButton = findViewById(R.id.navigationButton)
        mapButton.setOnClickListener {
            searchLoadToKakaoMap()
        }
    }

    private fun searchLoadToKakaoMap() {
        // 현재 위치를 LocationViewModel에서 가져옴


        val endLocation = Pair(toiletInfo.latitude, toiletInfo.longitude)

        Log.d("ToiletDetailView1", "Start Location: Latitude = ${startLocation?.first}, Longitude = ${startLocation?.second}")
        Log.d("ToiletDetailView1", "End Location: Latitude = ${endLocation.first}, Longitude = ${endLocation.second}")


        val url = "kakaomap://route?sp=${startLocation?.first},${startLocation?.second}&ep=${endLocation.first},${endLocation.second}&by=FOOT"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        val installCheck = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
            )
        } else {
            packageManager.queryIntentActivities(
                Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER),
                PackageManager.GET_META_DATA
            )
        }

        // 카카오맵이 설치되어 있다면 앱으로 연결, 설치되어 있지 않다면 스토어로 이동
        if (installCheck.isEmpty()) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.daum.android.map")))
        } else {
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }
}