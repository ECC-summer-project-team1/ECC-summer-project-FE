package com.example.toiletfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback

class ExploreFragment : Fragment() {

   /* private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_explore_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            override fun onMapReady(kakaoMap: KakaoMap) {
                // 인증 후 API가 정상적으로 실행될 때 호출됨
                // KakaoMap을 사용하여 필요한 작업을 추가합니다.
            }
        })
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
    }*/
}
