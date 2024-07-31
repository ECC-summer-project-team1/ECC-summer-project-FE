package com.example.toiletfinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RVAdapterforNearme(val items: MutableList<ToiletInfo>) : RecyclerView.Adapter<RVAdapterforNearme.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RVAdapterforNearme.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_nearme_toilet, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RVAdapterforNearme.ViewHolder, position: Int) {
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val nameTextView: TextView = itemView.findViewById(R.id.toilet_name)
        private val addressTextView: TextView = itemView.findViewById(R.id.toilet_address)

        fun bindItems(item: ToiletInfo) {
            nameTextView.text = item.name
            addressTextView.text = item.address

          /*  // 여기에 지도 프래그먼트를 초기화하고 설정하는 코드 추가
            val fragmentManager = (itemView.context as FragmentActivity).supportFragmentManager
            val mapFragment = fragmentManager.findFragmentById(R.id.map_fragment_container) as? SupportMapFragment
            if (mapFragment == null) {
                val newMapFragment = SupportMapFragment.newInstance()
                fragmentManager.beginTransaction()
                    .replace(R.id.map_fragment_container, newMapFragment)
                    .commit()

                newMapFragment.getMapAsync { googleMap ->
                    // 여기에 지도 설정 코드를 추가합니다.
                    // 예를 들어:
                    // val location = LatLng(item.latitude, item.longitude)
                    // googleMap.addMarker(MarkerOptions().position(location).title(item.name))
                    // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                }*/
        }
    }
}