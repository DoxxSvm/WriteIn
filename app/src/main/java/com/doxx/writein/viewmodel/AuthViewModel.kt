package com.doxx.writein.viewmodel

import android.text.TextUtils
import android.util.Patterns
import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doxx.writein.models.UserRequest
import com.doxx.writein.models.UserResponse
import com.doxx.writein.repository.UserRepository
import com.doxx.writein.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository):ViewModel() {
    val userResponseLiveData :LiveData<NetworkResult<UserResponse>>
    get() = userRepository.userResponseLiveData

    fun registerUser(userRequest: UserRequest){
        viewModelScope.launch {
            userRepository.registerUser(userRequest)
        }
    }
    fun loginUser(userRequest: UserRequest){
        viewModelScope.launch {
            userRepository.loginUser(userRequest)
        }
    }
    fun validateCredentials(email:String,password:String,username:String,isLogin:Boolean):Pair<Boolean,String>{
        var result = Pair(true,"")
        if((!isLogin && TextUtils.isEmpty(username)) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            result = Pair(false,"Please provide credentials")
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            result = Pair(false,"Please provide valid email")
        }
        else if(password.length <=5) {
            result = Pair(false,"Password length should be greater than 5")
        }
        return result
    }
}