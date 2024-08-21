package com.example.toiletfinder

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.MapView
import java.io.Serializable

class ToiletDetailView : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toilet_detail_view)

        fun <T: Serializable> Intent.intentSerializable(key: String, clazz: Class<T>): T? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                this.getSerializableExtra(key, clazz)
            } else {
                this.getSerializableExtra(key) as T?
            }
        }

        val toiletInfo = intent.getSerializableExtra("toilet_info") as ToiletInfo

        mapView = findViewById(R.id.detail_map_view)
        MapViewHelper.initializeMapView(mapView, toiletInfo.latitude, toiletInfo.longitude) { kakaoMap ->
            // 추가적인 설정이 필요하다면 여기에 작성
        }

        // Set up UI
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val backButton: TextView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
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
