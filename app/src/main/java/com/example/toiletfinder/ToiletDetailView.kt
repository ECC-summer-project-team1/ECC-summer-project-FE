package com.example.toiletfinder

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ToiletDetailView: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toilet_detail_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val backButton: TextView = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }
    }
}

