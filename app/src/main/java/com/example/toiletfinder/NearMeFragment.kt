package com.example.toiletfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class NearMeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트 레이아웃을 인플레이트합니다.
        val view = inflater.inflate(R.layout.activity_near_me_fragment, container, false)

        // RecyclerView 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_nearme)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 예시 데이터를 설정합니다.
        val toiletInfoList = mutableListOf(
            ToiletInfo("1", "9:00", "21:00", "화장실 1", "서울특별시 ...", 37.5665, 126.9780),
            ToiletInfo("2", "10:00", "18:00" , "화장실 2", "서울특별시 ...", 37.5675, 126.9790)
            // 추가 데이터
        )

        // 어댑터를 설정합니다.
        val adapter = RVAdapterforNearme(toiletInfoList)
        recyclerView.adapter = adapter

        return view;
    }
}