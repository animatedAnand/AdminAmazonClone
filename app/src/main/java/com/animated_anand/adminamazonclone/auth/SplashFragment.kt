package com.animated_anand.adminamazonclone.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.animated_anand.adminamazonclone.R
import com.animated_anand.adminamazonclone.activity.AdminMainActivity
import com.animated_anand.adminamazonclone.databinding.FragmentSplashBinding
import com.animated_anand.adminamazonclone.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private val viewModel : AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(layoutInflater)
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                viewModel.isUserSignedIn.collect{isUserSignedIn->
                    if(isUserSignedIn)
                    {
                        startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                        requireActivity().finish()
                    }
                    else
                    {
                        findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
                    }
                }
            }
        },1000)
        return binding.root
    }

}