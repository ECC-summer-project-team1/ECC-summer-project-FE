package com.example.mapfilter

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MapActivity : AppCompatActivity() {

    private lateinit var btnMap: Button
    private lateinit var btnRadius: Button
    private lateinit var radiusPopup: LinearLayout
    private lateinit var applyRadius: Button
    private lateinit var radius100m: CheckBox
    private lateinit var radius500m: CheckBox
    private lateinit va28r radius1km: CheckBox
    private lateinit var radius2km: CheckBox
    private lateinit var radius3km: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        btnMap = findViewById(R.id.btnMap)
        btnRadius = findViewById(R.id.btnRadius)
        radiusPopup = findViewById(R.id.radiusPopup)
        applyRadius = findViewById(R.id.applyRadius)
        radius100m = findViewById(R.id.radius100m)
        radius500m = findViewById(R.id.radius500m)
        radius1km = findViewById(R.id.radius1km)
        radius2km = findViewById(R.id.radius2km)
        radius3km = findViewById(R.id.radius3km)

        btnRadius.setOnClickListener {
            if (radiusPopup.visibility == View.GONE) {
                radiusPopup.visibility = View.VISIBLE
            } else {
                radiusPopup.visibility = View.GONE
            }
        }

        applyRadius.setOnClickListener {
            // 현재 선택된 체크박스를 확인하고 그대로 유지
            val selectedRadius = when {
                radius100m.isChecked -> "100m"
                radius500m.isChecked -> "500m"
                radius1km.isChecked -> "1km"
                radius2km.isChecked -> "2km"
                radius3km.isChecked -> "3km"
                else -> "none"
            }

            // 선택한 반경에 따라 필요한 작업을 수행합니다.
            radiusPopup.visibility = View.GONE
        }
    }
}
