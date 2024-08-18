package com.example.toiletfinder

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.kakao.vectormap.KakaoMapSdk
import com.kakao.vectormap.LatLng
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.MotionEvent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST


class MainActivity : AppCompatActivity() {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private var requestingLocationUpdates = false
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback


    // SharedPreferences
    private lateinit var sharedPreferences: SharedPreferences


    private val locationPermissions = arrayOf(
        ACCESS_COARSE_LOCATION,
        ACCESS_FINE_LOCATION
    )

    private lateinit var locationViewModel: LocationViewModel

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY)
        setContentView(R.layout.activity_main)

        //SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("RadiusPreferences", MODE_PRIVATE)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ViewModel 초기화 (필요에 따라 초기화 방식 변경)
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)

        checkLocationPermissions()
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

        // 체크박스를 배열로 묶어서 관리
        val checkBoxes = arrayOf(radius100m, radius500m, radius1km, radius2km, radius3km)

        // 체크박스 선택 시 다른 체크박스 해제
        for (checkBox in checkBoxes) {
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // 현재 선택된 체크박스를 제외한 나머지 체크박스를 모두 해제
                    checkBoxes.forEach { if (it != buttonView) it.isChecked = false }
                }
            }
        }

        buttonNearMe.setOnClickListener {
            setActive(NearMeFragment())
        }

        findViewById<Button>(R.id.button_explore).setOnClickListener {
            setActive(ExploreFragment())
        }

        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    for (location in locationResult.locations) {
                        val latLng = LatLng.from(location.latitude, location.longitude)
                        locationViewModel.updateLocation(latLng)
                    }
                } else {
                    Log.d("LocationCallback", "No location received")
                }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                Log.d("LocationCallback", "Location availability: ${availability.isLocationAvailable}")
            }
        }

        btnRadius.setOnClickListener {
            if (radiusPopup.visibility == View.VISIBLE) {
                radiusPopup.visibility = View.GONE
            } else {
                radiusPopup.visibility = View.VISIBLE
            }
        }

        resetRadius.setOnClickListener {
            checkBoxes.forEach { it.isChecked = false }
        }

        applyRadius.setOnClickListener {
            saveRadiusSelection(checkBoxes) // 주석: 반경 저장 함수 호출
            sendRadiusToBackend() // 주석: 서버로 전송 함수 호출
            // 반경설정 로직 추가
            radiusPopup.visibility = View.GONE
        }


        // 팝업 창 밖의 터치 이벤트 처리
        val mainLayout: ConstraintLayout = findViewById(R.id.main)
        mainLayout.setOnTouchListener { _, event ->
            if (radiusPopup.visibility == View.VISIBLE && event.action == MotionEvent.ACTION_DOWN) {
                radiusPopup.visibility = View.GONE
            }
            true
        }
    }

    // 주석: 사용자가 선택한 반경을 SharedPreferences에 저장하는 함수
    private fun saveRadiusSelection(checkBoxes: Array<CheckBox>) {
        val editor = sharedPreferences.edit()
        for (checkBox in checkBoxes) {
            if (checkBox.isChecked) {
                val selectedRadius = checkBox.text.toString()
                editor.putString("selectedRadius", selectedRadius)
                editor.apply()
                // 저장된 반경 값을 로그로 출력
                Log.d("MainActivity", "Selected radius saved: $selectedRadius")
                break
            }
        }
        editor.apply()
    }

    // 주석: 저장된 반경을 서버로 전송하는 함수
    private fun sendRadiusToBackend() {
        val selectedRadius = sharedPreferences.getString("selectedRadius", "100m")

        // 주석: Retrofit을 사용하여 서버로 데이터를 전송
        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-backend-url.com") // 실제 백엔드 URL로 변경하세요
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.sendRadiusData(selectedRadius ?: "100m")

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Radius data sent successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Failed to send radius data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startLocationUpdates() {
        if (!requestingLocationUpdates) {
            requestingLocationUpdates = true
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun checkLocationPermissions() {
        if (locationPermissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }) {
            getCurLocation()
        } else {
            requestLocationPermissions()
        }
    }

    private fun requestLocationPermissions() {
        requestPermissionsLauncher.launch(locationPermissions)
    }

    private val requestPermissionsLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                getCurLocation()
            } else {
                showPermissionDeniedDialog()
            }
        }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setMessage("위치 권한 거부시 앱을 사용할 수 없습니다.")
            .setPositiveButton("권한 설정하러 가기") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:$packageName"))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                    startActivity(intent)
                } finally {
                    finish()
                }
            }
            .setNegativeButton("앱 종료하기") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun getCurLocation() {
        if (locationPermissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }) {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    location?.let {
                        val latLng = LatLng.from(it.latitude, it.longitude)
                        locationViewModel.updateLocation(latLng)
                        startLocationUpdates() // 위치 권한이 부여된 후 위치 업데이트 시작
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("LocationError", "Failed to get location", e)
                }
        }
    }

    private fun setActive(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit()
    }

    //Retrofit API 인터페이스 정의
    interface ApiService {
        @POST("/api/sendRadiusData")
        fun sendRadiusData(@Body radius: String): Call<ResponseBody>
    }
}
