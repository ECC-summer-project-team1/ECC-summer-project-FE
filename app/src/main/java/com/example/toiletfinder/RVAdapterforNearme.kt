package com.example.toiletfinder

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.TransformMethod
import java.io.Serializable


class RVAdapterforNearme(private val items: MutableList<ToiletInfo>) : RecyclerView.Adapter<RVAdapterforNearme.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_nearme_toilet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.toilet_name)
        private val addressTextView: TextView = itemView.findViewById(R.id.toilet_address)
        private val mapView: MapView = itemView.findViewById(R.id.detail_map_view)
        private var kakaoMap: KakaoMap? = null
        private var locationLabel: Label? = null



        fun bindItems(item: ToiletInfo) {
            nameTextView.text = item.toiletName
            addressTextView.text = item.addressRoad

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

                    val initialPosition = LatLng.from(item.latitude, item.longitude)

                    initialPosition.let { position ->
                        kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(position))

                        val labelLayer = kakaoMap?.getLabelManager()?.layer

                        // 화장실 위치를 표시할 라벨 생성
                        locationLabel = labelLayer?.addLabel(
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
                }
            })


            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ToiletDetailView::class.java).apply {
                    putExtra("toilet_info", item as Serializable)
                }
                context.startActivity(intent)
            }
        }
    }
}