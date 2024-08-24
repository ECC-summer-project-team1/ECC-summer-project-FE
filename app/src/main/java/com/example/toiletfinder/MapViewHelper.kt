package com.example.toiletfinder

import com.kakao.vectormap.GestureType
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.TransformMethod

object MapViewHelper {

    fun initializeMapView(
        mapView: MapView,
        latitude: Double,
        longitude: Double,
        onMapReady: (KakaoMap) -> Unit
    ) {
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출됨
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
                error.printStackTrace()
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                onMapReady(map)
                val position = LatLng.from(latitude, longitude)
                map.moveCamera(CameraUpdateFactory.newCenterPosition(position))

                map!!.setGestureEnable(GestureType.OneFingerDoubleTap, false)
                map!!.setGestureEnable(GestureType.TwoFingerSingleTap, false)
                map!!.setGestureEnable(GestureType.Zoom, false)
                map!!.setGestureEnable(GestureType.OneFingerZoom, false)
                map!!.setGestureEnable(GestureType.Pan, false)
                map!!.setGestureEnable(GestureType.Tilt, false)
                map!!.setGestureEnable(GestureType.Rotate, false)


                val labelLayer = map.getLabelManager()?.layer

                // 화장실 위치를 표시할 라벨 생성
                labelLayer?.addLabel(
                    LabelOptions.from(position)
                        .setRank(10)
                        .setStyles(
                            LabelStyles.from(
                                LabelStyle.from(R.drawable.map_pin)
                                    .setAnchorPoint(0.5f, 0.5f)
                            )
                        )
                        .setTransform(TransformMethod.AbsoluteRotation_Decal)
                )
            }
        })

    }

}