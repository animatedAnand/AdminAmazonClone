package com.animated_anand.adminamazonclone.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.animated_anand.adminamazonclone.models.Admin
import com.animated_anand.adminamazonclone.utils.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel :ViewModel()
{
    private val _verificationID = MutableStateFlow<String?>(null)
    private val _otp = MutableStateFlow(false)
    val otpSent = _otp
    private val _isSignedUpSuccesfully = MutableStateFlow(false)
    val isSignedUpSuccesfully = _isSignedUpSuccesfully

    private val _isUserSignedIn = MutableStateFlow(false)
    val isUserSignedIn = _isUserSignedIn

    init {
        Utils.getFirebaseAuthInstance().currentUser.let {
            if(it != null)
            {
                isUserSignedIn.value = true
            }
        }
    }

    fun sendOTP(phoneNumber : String, activity : Activity)
    {
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                TODO("Not yet implemented")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                TODO("Not yet implemented")
            }

            override fun onCodeSent(verificationID : String, token : PhoneAuthProvider.ForceResendingToken)
            {
                _verificationID.value = verificationID
                otpSent.value = true
            }

        }

        val options = PhoneAuthOptions.newBuilder(Utils.getFirebaseAuthInstance())
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signinWithPhoneAuthCredential(enteredOTP: String, currentUser: Admin) {
        val credential = PhoneAuthProvider.getCredential(_verificationID.value.toString(),enteredOTP)
        Utils.getFirebaseAuthInstance()
            .signInWithCredential(credential)
            .addOnCompleteListener {task->
                currentUser.uid = Utils.getCurrentUserID()
                if(task.isSuccessful)
                {
                    _isSignedUpSuccesfully.value = true
                    FirebaseDatabase.getInstance().getReference("AllAdmins").child("AdminInfo")
                        .child(currentUser.uid!!).setValue(currentUser)
                }
            }
    }
}