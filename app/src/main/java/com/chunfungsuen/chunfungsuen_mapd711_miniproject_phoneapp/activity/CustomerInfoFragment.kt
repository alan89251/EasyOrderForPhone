package com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chunfungsuen.chunfungsuen_mapd711_miniproject_phoneapp.R

class CustomerInfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_info, container, false)
    }
}