package com.animated_anand.adminamazonclone.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.animated_anand.adminamazonclone.R
import com.animated_anand.adminamazonclone.activity.AdminMainActivity
import com.animated_anand.adminamazonclone.databinding.FragmentOTPBinding
import com.animated_anand.adminamazonclone.models.Admin
import com.animated_anand.adminamazonclone.utils.Utils
import com.animated_anand.adminamazonclone.viewmodels.AuthViewModel
import kotlinx.coroutines.launch


class OTPFragment : Fragment() {

    private lateinit var userNumber :String
    private lateinit var binding: FragmentOTPBinding
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var listOtpEditTexts : Array<EditText>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(layoutInflater)
        listOtpEditTexts = arrayOf(binding.etOtp0,binding.etOtp1,binding.etOtp2,binding.etOtp3,binding.etOtp4,binding.etOtp5)

        getUserNumber()
        customizingEnteringOtp()
        sendOTP()
        onLoginButtonClicked()
        onBackButtonClicked()
        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.btLogin.setOnClickListener {
            Utils.showProgressDialog(requireContext(),"Signing you..")
            var enteredOTP = ""
            listOtpEditTexts.forEach {digit->
                enteredOTP+= digit.text
            }
            if(enteredOTP.length != 6)
            {
                Utils.hideProgressDialog()
                Utils.showToast(requireContext(),"Invalid OTP entered")
            }
            else
            {
                verifyOTP(enteredOTP)
            }
        }
    }

    private fun verifyOTP(enteredOTP: String) {
        val currentUserID = Utils.getCurrentUserID()
        var currentAdmin = Admin(null,userNumber)
        viewModel.signinWithPhoneAuthCredential(enteredOTP,currentAdmin)
        lifecycleScope.launch {
            viewModel.isSignedUpSuccesfully.collect{
                if(it)
                {
                    Utils.hideProgressDialog()
                    Utils.showToast(requireContext(),"Succesfully signed!")
                    startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    private fun sendOTP() {
        Utils.showProgressDialog(requireContext(),"Sending OTP..")
        viewModel.sendOTP(userNumber,requireActivity())
        viewModel.apply {
            lifecycleScope.launch {
                otpSent.collect {
                    if(it)
                    {
                        Utils.hideProgressDialog()
                        Utils.showToast(requireContext(),"OTP Sent")
                    }
                }
            }
        }
    }

    private fun onBackButtonClicked() {
        binding.tbOtpFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
        }
    }

    private fun customizingEnteringOtp() {
        for(i in listOtpEditTexts.indices)
        {
            listOtpEditTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int,after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if(s?.length == 1 && i<5)
                    {
                        listOtpEditTexts[i+1].requestFocus()
                    }
                    else if(s?.length == 0 && i>0)
                    {
                        listOtpEditTexts[i-1].requestFocus()
                    }
                }

            })

        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()
        binding.tvUserNumber.text = "+91 "+ userNumber
    }

}