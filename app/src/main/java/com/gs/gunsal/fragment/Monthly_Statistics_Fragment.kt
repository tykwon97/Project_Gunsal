package com.gs.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gs.gunsal.databinding.FragmentMonthlyStatisticsBinding

class Monthly_Statistics_Fragment : Fragment() {

    var binding: FragmentMonthlyStatisticsBinding?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMonthlyStatisticsBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}