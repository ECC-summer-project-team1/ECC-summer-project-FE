package com.example.toiletfinder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.TransformMethod
import com.kakao.vectormap.shape.Polygon

class ExploreFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var locationViewModel: LocationViewModel
    private var kakaoMap: KakaoMap? = null
    private var locationLabel: Label? = null
    private lateinit var backendManager: BackendManager

    private var isTracking = true  // 현재 트래킹 중인지 여부를 나타내는 플래그


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_explore_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel 초기화
        locationViewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)
        backendManager = BackendManager(requireContext(), locationViewModel)

        // MapView 초기화
        mapView = view.findViewById(R.id.map_view)
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출됨
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
                error.printStackTrace()  // 에러 로그를 확인합니다.
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                kakaoMap = map
                kakaoMap?.getCompass()?.show()

                val initialPosition = locationViewModel.location.value?.let {
                    LatLng.from(it.latitude, it.longitude)
                } ?: kakaoMap?.cameraPosition?.position

                initialPosition?.let { position ->
                    kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(position))

                    val labelLayer = kakaoMap?.getLabelManager()?.layer

                    // 현재 위치를 표시할 라벨 생성
                    locationLabel = labelLayer?.addLabel(
                        LabelOptions.from(position)
                            .setRank(10)
                            .setStyles(
                                LabelStyles.from(
                                    LabelStyle.from(R.drawable.cur_poslabel)
                                        .setAnchorPoint(0.5f, 0.5f)
                                )
                            )
                            .setTransform(TransformMethod.AbsoluteRotation_Decal)
                    )

                    //toilet 위치
                    locationViewModel.toiletList.observe(viewLifecycleOwner) { toilets ->
                        toilets.forEach { toilet ->
                            val toiletPosition = LatLng.from(toilet.latitude, toilet.longitude)

                            // 각 화장실 위치에 라벨을 추가합니다.
                            labelLayer?.addLabel(
                                LabelOptions.from(toiletPosition)
                                    .setRank(10)
                                    .setStyles(
                                        LabelStyles.from(
                                            LabelStyle.from(R.drawable.map_pin) // 화장실 라벨 아이콘을 사용하세요.
                                                .setAnchorPoint(0.5f, 0.5f)
                                        )
                                    )
                                    .setTransform(TransformMethod.AbsoluteRotation_Decal)
                            )
                        }
                    }
                }

                kakaoMap?.setOnCameraMoveStartListener { map, gestureType ->
                    Log.d("ExploreFragment", "Camera move started. GestureType: $gestureType")
                    if (gestureType == GestureType.Pan) {
                        isTracking = false
                    }
                }
            }
        })

        // ViewModel의 location 값이 변경될 때마다 라벨 위치 업데이트
        locationViewModel.location.observe(viewLifecycleOwner) { latLng ->
            if (latLng != null) {
                // 지도의 카메라를 새로운 위치로 이동
                val newPosition = LatLng.from(latLng.latitude, latLng.longitude)
                if (isTracking) {
                    kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(newPosition))
                }
                // 라벨의 위치를 새 위치로 이동
                locationLabel?.moveTo(newPosition)
            } else {
                Log.d("ExploreFragment", "Location is currently null")
            }
        }

        view.findViewById<ImageButton>(R.id.reset_button).setOnClickListener {
            // 새로운 위치로 카메라 트래킹 시작
            locationViewModel.location.value?.let { latLng ->
                val newPosition = LatLng.from(latLng.latitude, latLng.longitude)
                kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(newPosition))
                // 라벨의 위치를 새 위치로 이동
                locationLabel?.moveTo(newPosition)
                backendManager.sendCurrentInfoOnce("500")
                isTracking = true  // 버튼 클릭 시 트래킹 상태를 활성화
            }
        }

        val findToiletButton: Button = view.findViewById(R.id.find_toilet_button)
        findToiletButton.setOnClickListener {
            kakaoMap?.cameraPosition?.let { cameraPosition ->
                Log.d("ExploreFragment", "Camera Position: Latitude = ${cameraPosition.position.latitude}, Longitude = ${cameraPosition.position.longitude}")
                val latLng = LatLng.from(cameraPosition.position.latitude, cameraPosition.position.longitude)
                backendManager.sendCameraInfoOnce(latLng, "500")
            }
        }
    }


    override fun onResume() {
        super.onResume()
        mapView.resume() // MapView의 resume 호출
    }

    override fun onPause() {
        super.onPause()
        mapView.pause() // MapView의 pause 호출
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 필요한 경우 리소스 정리 및 MapView 관련 작업 수행
    }
}
