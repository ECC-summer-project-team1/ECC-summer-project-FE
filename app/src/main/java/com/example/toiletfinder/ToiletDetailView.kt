package com.example.toiletfinder

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ToiletDtailActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toilet_detail_view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val backButton: TextView = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            finish()  // 현재 Activity 종료
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent().apply {
                    // 원하는 코드 입력
                }
                setResult(RESULT_OK, intent)  // 원하는 코드 입력
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}