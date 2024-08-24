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
import android.view.inputmethod.EditorInfo
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

    private lateinit var backendManager: BackendManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_MAP_KEY)
        setContentView(R.layout.activity_main)

        locationViewModel  = ViewModelProvider(this).get(LocationViewModel::class.java)
        backendManager = BackendManager(this, locationViewModel )

        //SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("RadiusPreferences", MODE_PRIVATE)

        val fileUri: Uri = Uri.parse("file://path_to_your_file/yourfile.xlsx") // TODO: 실제 파일 경로로 수정

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 위치 권한 확인
        checkLocationPermissions()

        // EditText와 ImageButton 초기화
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.btnSearch)

        // 검색 버튼 클릭 이벤트 처리
        searchButton.setOnClickListener {
            if (searchEditText.visibility == View.GONE) {
                searchEditText.visibility = View.VISIBLE
            } else {
                executeSearch()
            }
        }
        // Enter 키로 검색 실행
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                executeSearch()
                true  // 줄바꿈을 막고, 검색 동작을 실행
            } else {
                false
            }
        }


        // 초기 Fragment 설정
        setActive(ExploreFragment())

        // 버튼 및 반경 설정 관련 UI 초기화 및 설정
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

        // 체크박스 배열로 관리
        val checkBoxes = arrayOf(radius100m, radius500m, radius1km, radius2km, radius3km)
        radius100m.isChecked = true //기본값으로 설정

        // 체크박스 선택 시 다른 체크박스 해제
        for (checkBox in checkBoxes) {
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // 현재 선택된 체크박스를 제외한 나머지 체크박스를 모두 해제
                    checkBoxes.forEach { if (it != buttonView) it.isChecked = false }
                }
            }
        }

        //시작하자마자 기본적으로 전송함.
        val selectedRadius = saveRadiusSelection(checkBoxes)
        if (selectedRadius != null) {
            backendManager.sendCurrentInfoOnce(selectedRadius)
        }

        // "Near Me" 버튼 클릭 이벤트 설정
        buttonNearMe.setOnClickListener {
            setActive(NearMeFragment())
        }

        // "Explore" 버튼 클릭 이벤트 설정
        buttonExplore.setOnClickListener {
            setActive(ExploreFragment())
        }

        // 위치 요청 및 콜백 설정
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
            .setMinUpdateIntervalMillis(2000L) // 위치 업데이트 간격을 2초로 설정
            .setMaxUpdateDelayMillis(2000L) // 최대 업데이트 지연 시간도 2초로 설정
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

        // 반경 설정 버튼 클릭 이벤트 설정
        btnRadius.setOnClickListener {
            if (radiusPopup.visibility == View.VISIBLE) {
                radiusPopup.visibility = View.GONE
            } else {
                radiusPopup.visibility = View.VISIBLE
            }
        }

        // 반경 초기화 버튼 클릭 이벤트 설정
        resetRadius.setOnClickListener {
            checkBoxes.forEach { it.isChecked = false }
        }

        // 반경 적용 버튼 클릭 이벤트 설정
        applyRadius.setOnClickListener {
            val selectedRadius = saveRadiusSelection(checkBoxes)
            if (selectedRadius != null) {
                backendManager.sendCurrentInfoOnce(selectedRadius)
            } else {
                Toast.makeText(this, "반경을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
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

    // 검색어를 백엔드로 전송
    private fun executeSearch() {
        val query = searchEditText.text.toString().trim()
        if (query.isNotEmpty()) {
            saveSearchQuery(query)  // 검색어 저장
            sendSearchQueryToBackend(query)

            // 검색어 저장 이후 EditText 초기화 및 숨기기
            searchEditText.setText("")
            searchEditText.visibility = View.GONE
        } else {
            Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 검색어를 SharedPreferences에 저장
    private fun saveSearchQuery(query: String) {
        val editor = sharedPreferences.edit()
        editor.putString("lastSearchQuery", query)
        editor.apply()

        // 저장된 검색어 값을 로그로 출력
        Log.d("MainActivity", "Search query saved: $query")
    }

    // 검색어를 백엔드로 전송하는 함수
    private fun sendSearchQueryToBackend(query: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-backend-url.com") // 실제 백엔드 URL로 변경필요ㅇ
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.search(query)

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val searchResults = response.body()?.results ?: emptyList()
                    displaySearchResults(searchResults)
                } else {
                    Toast.makeText(this@MainActivity, "검색에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 검색 결과를 UI에 표시하는 함수
    private fun displaySearchResults(results: List<SearchResult>) {
        if (results.isNotEmpty()) {
            Toast.makeText(this, "첫 번째 결과: ${results[0].title}", Toast.LENGTH_LONG).show()
            // RecyclerView 등을 사용하여 결과를 표시하세요.
        } else {
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 주석: 사용자가 선택한 반경을 SharedPreferences에 저장하는 함수
    private fun saveRadiusSelection(checkBoxes: Array<CheckBox>): String? {
        var selectedRadius: String? = null
        val editor = sharedPreferences.edit()
        for (checkBox in checkBoxes) {
            if (checkBox.isChecked) {
                selectedRadius = checkBox.text.toString()
                editor.putString("selectedRadius", selectedRadius)
                editor.apply()
                Log.d("MainActivity", "Selected radius saved: $selectedRadius")
                break
            }
        }
        return selectedRadius
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

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        //fusedLocationClient.removeLocationUpdates(locationCallback)
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

}
