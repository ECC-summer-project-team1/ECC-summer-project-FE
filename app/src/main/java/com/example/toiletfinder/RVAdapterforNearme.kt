package com.example.toiletfinder

import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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

        fun bindItems(item: ToiletInfo) {
            nameTextView.text = item.name
            addressTextView.text = item.address

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ToiletDetailView::class.java).apply {
                    putExtra("toilet_info", item as Parcelable)
                }
                context.startActivity(intent)
            }
        }
    }
}