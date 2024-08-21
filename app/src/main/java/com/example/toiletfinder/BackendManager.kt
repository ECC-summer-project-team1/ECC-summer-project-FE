package com.example.toiletfinder

import android.content.Context
import android.net.Uri
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

    private var job: Job? = null

    fun startSendingRadiusData(fileUri: Uri, radius: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val latLng = viewModel.location.value
                if (latLng != null) {
                    sendRadiusData(fileUri, latLng, radius)
                }
                delay(10000) // 10초마다 전송
            }
        }
    }

    fun stopSendingRadiusData() {
        job?.cancel()
    }


    private fun sendRadiusData(fileUri: Uri, latLng: LatLng, radius: String) {
        val file = File(fileUri.path ?: "")
        val requestFile =
            file.asRequestBody("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val latPart = latLng.latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val lonPart = latLng.longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val radiusPart = radius.toRequestBody("text/plain".toMediaTypeOrNull())

        val call = apiService.sendRadiusData(body, latPart, lonPart, radiusPart)

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
}