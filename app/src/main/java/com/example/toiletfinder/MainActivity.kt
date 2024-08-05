package com.example.toiletfinder

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.kakao.vectormap.KakaoMapSdk
import com.example.toiletfinder.BuildConfig


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setActive(ExploreFragment())

        val buttonNearMe: Button = findViewById<Button>(R.id.button_nearme)
        val buttonExplore: Button = findViewById<Button>(R.id.button_explore)

        buttonNearMe.setOnClickListener {
            setActive(NearMeFragment())
        }

        buttonExplore.setOnClickListener {
            setActive(ExploreFragment())
        }
    }


    private fun setActive(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_frame, fragment)
        fragmentTransaction.commit()
    }
}
