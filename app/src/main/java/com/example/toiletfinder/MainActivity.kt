package com.example.toiletfinder

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
<<<<<<< Updated upstream
=======
import com.kakao.vectormap.KakaoMapSdk
import com.example.toiletfinder.BuildConfig
import com.kakao.vectormap.BuildConfig
>>>>>>> Stashed changes

class MainActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // EditText와 ImageButton 초기화
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.btnSearch)

        // ImageButton 클릭 이벤트 처리
        searchButton.setOnClickListener {
            if (searchEditText.visibility == View.GONE) {
                searchEditText.visibility = View.VISIBLE
            } else {
                searchEditText.visibility = View.GONE
            }
        }

        setActive(ExploreFragment())

        val buttonNearMe: Button = findViewById(R.id.button_nearme)
        val buttonExplore: Button = findViewById(R.id.button_explore)
        val btnRadius: Button = findViewById(R.id.btnRadius)
        val radiusPopup: LinearLayout = findViewById(R.id.radiusPopup)
        val resetRadius: Button = findViewById(R.id.resetRadius)
        val applyRadius: Button = findViewById(R.id.applyRadius)
        val radius100m: CheckBox = findViewById(R.id.radius100m)
        val radius500m: CheckBox = findViewById(R.id.radius500m)
        val radius1km: CheckBox = findViewById(R.id.radius1km)
        val radius2km: CheckBox = findViewById(R.id.radius2km)
        val radius3km: CheckBox = findViewById(R.id.radius3km)

        buttonNearMe.setOnClickListener {
            setActive(NearMeFragment())
        }

        buttonExplore.setOnClickListener {
            setActive(ExploreFragment())
        }

        btnRadius.setOnClickListener {
            if (radiusPopup.visibility == View.VISIBLE) {
                radiusPopup.visibility = View.GONE
            } else {
                radiusPopup.visibility = View.VISIBLE
            }
        }

        resetRadius.setOnClickListener {
            radius100m.isChecked = false
            radius500m.isChecked = false
            radius1km.isChecked = false
            radius2km.isChecked = false
            radius3km.isChecked = false
        }

        applyRadius.setOnClickListener {
            // 반경설정 로직 추가
            radiusPopup.visibility = View.GONE
        }
    }

    private fun setActive(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_frame, fragment)
        fragmentTransaction.commit()
    }
}
