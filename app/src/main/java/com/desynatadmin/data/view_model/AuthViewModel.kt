package com.desynatadmin.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatadmin.data.repo.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val progress: LiveData<Boolean> get() = repository.progress
    val error: LiveData<String> get() = repository.error
    fun reset() {
        repository.error.value = null
        repository.progress.value = false
    }

    val loginResult: LiveData<LoginResponse>
        get() = repository.loginLiveData

    fun login(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.login(map)
        }
    }

    val otpVerifyResult: LiveData<LoginResponse>
        get() = repository.otpVerifyLiveData

    fun otpVerification(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.otpVerification(map)
        }
    }


    val resendOtpResult: LiveData<LoginResponse>
        get() = repository.resendOtpLiveData

    fun resendOtp(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.resendOtp(map)
        }
    }


    val forgotPasswordResult: LiveData<LoginResponse>
        get() = repository.forgotPasswordLiveData

    fun forgotPassword(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.forgotPassword(map)
        }
    }

    val createPasswordResult: LiveData<LoginResponse>
        get() = repository.createPasswordLiveData

    fun createPassword(map: HashMap<String,Any>) {
        viewModelScope.launch {
            repository.createPassword(map)
        }
    }


}
