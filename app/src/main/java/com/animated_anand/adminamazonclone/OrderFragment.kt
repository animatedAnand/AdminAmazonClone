package com.animated_anand.adminamazonclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.animated_anand.adminamazonclone.databinding.FragmentHomeBinding
import com.animated_anand.adminamazonclone.databinding.FragmentOrderBinding

class OrderFragment : Fragment() {
    private lateinit var binding : FragmentOrderBinding
   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentOrderBinding.inflate(layoutInflater)
       return binding.root
    }

}