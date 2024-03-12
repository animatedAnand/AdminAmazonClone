package com.animated_anand.adminamazonclone.auth

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.animated_anand.adminamazonclone.R
import com.animated_anand.adminamazonclone.databinding.FragmentSignInBinding
import com.animated_anand.adminamazonclone.utils.Utils

class SignInFragment : Fragment() {

    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(layoutInflater)
        setStatusBarColor()
        getUserNumber()
        onContinueButtonClick()
        return binding.root
    }

    private fun onContinueButtonClick() {
        binding.btSignIn.setOnClickListener {
            val number = binding.etPhoneNumberSignIn.text.toString()
            if(number.isEmpty() || number.length!=10)
            {
                Utils.showToast(requireContext(),"Please insert valid phone number")
            }
            else
            {
                val bundle = Bundle()
                bundle.putString("number",number)
                findNavController().navigate(R.id.action_signInFragment_to_OTPFragment,bundle)
            }
        }
    }

    private fun getUserNumber() {
        binding.etPhoneNumberSignIn.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(numberlength: CharSequence?, start: Int, before: Int, count: Int) {
                if(numberlength?.length == 10)
                {
                    binding.btSignIn.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green))
                }
                else
                {
                    binding.btSignIn.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.grey))
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }

        )
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}