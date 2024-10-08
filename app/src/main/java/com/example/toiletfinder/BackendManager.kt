package com.example.toiletfinder

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.kakao.vectormap.LatLng
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class BackendManager(
    private val context: Context,
    private val viewModel: LocationViewModel
) {

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://your-backend-url.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    fun sendCurrentInfoOnce(radius: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val latLng = viewModel.location.value
            if (latLng != null) {
                sendCurrrentInfo(latLng, radius)
            }
            receiveDummyData() // 더미 데이터도 호출
        }
    }

    //현재 위치 말고 드래그한 곳
    fun sendCameraInfoOnce(latLng: LatLng, radius: String) {
        CoroutineScope(Dispatchers.IO).launch {
            sendCurrrentInfo(latLng, radius)
            receiveDummyData2() // 더미 데이터도 호출
        }
    }

    // 더미 데이터를 생성하고 ViewModel에 저장하는 함수입니다.
    private suspend fun receiveDummyData() {
        Log.d("BackendManager", "Generating dummy data")

        // 더미 데이터 생성
        val dummyData = listOf(
            ToiletInfo(
                category = "공중화장실",
                reference = "001",
                toiletName = "화장실 1",
                addressRoad = "서울특별시 종로구 세종대로 110",
                addressJibun = "서울특별시 종로구 세종로 1-68",
                maleToiletCount = 2,
                femaleToiletCount = 3,
                maleDisabledToiletCount = 1,
                femaleDisabledToiletCount = 1,
                maleChildToiletCount = 1,
                femaleChildToiletCount = 1,
                managingInstitution = "서울시청",
                managingPhoneNumber = "02-123-4567",
                openingHours = "06:00-22:00",
                latitude = 37.55324,
                longitude = 126.8685
            ),
            ToiletInfo(
                category = "공중화장실",
                reference = "002",
                toiletName = "화장실 2",
                addressRoad = "서울특별시 강남구 테헤란로 123",
                addressJibun = "서울특별시 강남구 역삼동 678-1",
                maleToiletCount = 3,
                femaleToiletCount = 4,
                maleDisabledToiletCount = 1,
                femaleDisabledToiletCount = 1,
                maleChildToiletCount = 0,
                femaleChildToiletCount = 1,
                managingInstitution = "강남구청",
                managingPhoneNumber = "02-987-6543",
                openingHours = "24시간",
                latitude = 37.5324,
                longitude = 126.8685
            )
        )

        // 더미 데이터를 ViewModel에 업데이트 (UI 스레드에서 실행)
        withContext(Dispatchers.Main) {
            Log.d("BackendManager", "Updating toilet list in ViewModel")
            viewModel.toiletList.value = dummyData
        }
    }

    private suspend fun receiveDummyData2() {
        Log.d("BackendManager", "Generating dummy data")

        // 더미 데이터 생성
        val dummyData2 = listOf(
            ToiletInfo(
                category = "공중화장실",
                reference = "001",
                toiletName = "오목공원 화장실",
                addressRoad = "서울특별시 양천구 목동서로 159-2",
                addressJibun = "서울특별시 양천구 목동서로 159-2",
                maleToiletCount = 2,
                femaleToiletCount = 3,
                maleDisabledToiletCount = 1,
                femaleDisabledToiletCount = 1,
                maleChildToiletCount = 1,
                femaleChildToiletCount = 1,
                managingInstitution = "양천구청",
                managingPhoneNumber = "02-123-4567",
                openingHours = "06:00-22:00",
                latitude = 37.5276,
                longitude = 126.8737
            ),
            ToiletInfo(
                category = "공중화장실",
                reference = "002",
                toiletName = "목동 교보문구 화장실",
                addressRoad = "서울특별시 양천구 목동서로 159-1",
                addressJibun = "서울특별시 양천구 목동서로 159-1",
                maleToiletCount = 3,
                femaleToiletCount = 4,
                maleDisabledToiletCount = 1,
                femaleDisabledToiletCount = 1,
                maleChildToiletCount = 0,
                femaleChildToiletCount = 1,
                managingInstitution = "건물주",
                managingPhoneNumber = "02-987-6543",
                openingHours = "24시간",
                latitude = 37.5283,
                longitude = 126.875
            )
        )

        // 더미 데이터를 ViewModel에 업데이트 (UI 스레드에서 실행)
        withContext(Dispatchers.Main) {
            Log.d("BackendManager", "Updating toilet list in ViewModel")
            viewModel.toiletList.value = dummyData2
        }
    }



    private fun sendCurrrentInfo(latLng: LatLng, radius: String) {
        val file = File("")
        val requestFile =
            file.asRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val latPart = latLng.latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lonPart = latLng.longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val radiusPart = radius.toRequestBody("text/plain".toMediaTypeOrNull())

        val call = apiService.sendCurrrentInfo(body, latPart, lonPart, radiusPart)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Data sent successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to send data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(context, "Network error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //정보를 받아오는 중
    fun fetchNearToiletData(latitude: Double, longitude: Double, radius: String) {
        val call = apiService.getToilets(latitude, longitude, radius)

        call.enqueue(object : Callback<List<ToiletInfo>> {
            override fun onResponse(call: Call<List<ToiletInfo>>, response: Response<List<ToiletInfo>>) {
                if (response.isSuccessful) {
                    val toilets = response.body()
                    if (toilets != null) {
                        viewModel.updateToiletList(toilets)
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ToiletInfo>>, t: Throwable) {
                Toast.makeText(context, "Network error occurred while fetching data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //검색했다는 전재. 자세한건 백엔드 연결할때 다시 봐야함. 더미데이터로 대신함.
    fun search(searchText: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (searchText != null) {
                //call할때. 백엔드 되면 채워야할듯.
            }
            receiveDummyData3() // 더미 데이터도 호출
        }
    }

    private suspend fun receiveDummyData3() {
        Log.d("BackendManager", "Generating dummy data")

        // 더미 데이터 생성
        val dummyData3 = listOf(
            ToiletInfo(
                category = "공중화장실",
                reference = "001",
                toiletName = "이화여대 개방 화장실",
                addressRoad = "서울특별시 대신동",
                addressJibun = "서울특별시 대신동",
                maleToiletCount = 2,
                femaleToiletCount = 3,
                maleDisabledToiletCount = 1,
                femaleDisabledToiletCount = 1,
                maleChildToiletCount = 1,
                femaleChildToiletCount = 1,
                managingInstitution = "양천구청",
                managingPhoneNumber = "02-123-4567",
                openingHours = "06:00-22:00",
                latitude = 37.5603,
                longitude = 126.9447
            ),
            ToiletInfo(
                category = "공중화장실",
                reference = "002",
                toiletName = "이화여대 부속 이동통신관 화장실",
                addressRoad = "서울특별시 양천구 목동서로 159-1",
                addressJibun = "서울특별시 양천구 목동서로 159-1",
                maleToiletCount = 3,
                femaleToiletCount = 4,
                maleDisabledToiletCount = 1,
                femaleDisabledToiletCount = 1,
                maleChildToiletCount = 0,
                femaleChildToiletCount = 1,
                managingInstitution = "이화여대",
                managingPhoneNumber = "02-987-6543",
                openingHours = "24시간",
                latitude = 37.5618,
                longitude = 126.9436
            )
        )

        // 더미 데이터를 ViewModel에 업데이트 (UI 스레드에서 실행)
        withContext(Dispatchers.Main) {
            Log.d("BackendManager", "Updating toilet list in ViewModel")
            viewModel.toiletList.value = dummyData3
        }
    }
}