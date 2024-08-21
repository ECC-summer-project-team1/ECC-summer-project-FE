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

        fun bindItems(item: ToiletInfo) {
            nameTextView.text = item.toiletName
            addressTextView.text = item.addressRoad

            MapViewHelper.initializeMapView(mapView, item.latitude, item.longitude) { map ->
                kakaoMap = map
                kakaoMap?.getCompass()?.show()
            }

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