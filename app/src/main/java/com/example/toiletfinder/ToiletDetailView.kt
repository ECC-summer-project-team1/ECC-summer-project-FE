package com.example.toiletfinder

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.Serializable

class ToiletDetailView: AppCompatActivity() {
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
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val backButton: TextView = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }
    }
}

