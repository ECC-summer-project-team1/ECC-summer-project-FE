package com.example.toiletfinder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class NearMeFragment : Fragment() {

    private lateinit var locationViewModel: LocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트 레이아웃을 인플레이트합니다.
        val view = inflater.inflate(R.layout.activity_near_me_fragment, container, false)
        locationViewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)

        // RecyclerView 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_nearme)
        recyclerView.layoutManager = LinearLayoutManager(context)


        // 어댑터를 설정합니다.
        val adapter = RVAdapterforNearme(mutableListOf(), locationViewModel)
        recyclerView.adapter = adapter

        locationViewModel.toiletList.observe(viewLifecycleOwner) { toilets ->
            Log.d("NearMeFragment", "Toilet list updated with ${toilets.size} items")

            (recyclerView.adapter as RVAdapterforNearme).apply {
                items.clear()
                items.addAll(toilets)
                notifyDataSetChanged()
            }
        }


        return view;
    }
}