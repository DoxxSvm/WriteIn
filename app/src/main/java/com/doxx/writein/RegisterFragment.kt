package com.doxx.writein

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.doxx.writein.databinding.FragmentRegisterBinding
import com.doxx.writein.models.UserRequest
import com.doxx.writein.utils.NetworkResult
import com.doxx.writein.utils.TokenManager
import com.doxx.writein.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding:FragmentRegisterBinding?= null
    private val binding get() = _binding!! //so that we don't have to use !! again and again
    private val viewModel by viewModels<AuthViewModel> ()

    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentRegisterBinding.inflate(inflater,container,false)
        if(tokenManager.getToken() != null){
            //Toast.makeText(context, "hii"+tokenManager.getToken().toString(), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        binding.btnSignUp.setOnClickListener{
            val matchingValidationResult = matchUserInput()
            if(matchingValidationResult.first){
                val validationResult = validateUserInput()
                if(validationResult.first){
                    viewModel.registerUser(getUserRequest())
                }
                else {
                    binding.txtError.text = validationResult.second
                }
            }
            else{
                binding.txtError.text=matchingValidationResult.second
            }

        }
        viewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = false
            when(it){
                is NetworkResult.Success ->{
                    tokenManager.saveToken(it.data!!.token)
                    findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
                }
                is NetworkResult.Error -> {
                    binding.txtError.text = it.message
                }
                is NetworkResult.Loading ->{
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun matchUserInput(): Pair<Boolean,String> {
        val result= Pair(true,"");
        val pass=binding.txtPassword.text.toString()
        val cnfPass = binding.txtConfirmPassword.text.toString()
        val secretKey = binding.secretKeyRegister.text.toString()
        val cnfSecretKey = binding.confirmSecretKeyRegister.text.toString()

        if(pass != cnfPass) return Pair(false,"Password didn't match")
        if(secretKey != cnfSecretKey) return Pair(false,"Secret key didn't match")
        return result
    }

    private fun getUserRequest():UserRequest{
        val email = binding.txtEmail.text.toString()
        val pass = binding.txtPassword.text.toString()
        val username = binding.txtUsername.text.toString()
        val secretKey = binding.secretKeyRegister.text.toString()
        saveSecretKey(secretKey)
        return UserRequest(email,pass,username)
    }

    private fun saveSecretKey(secretKey: String) {
        val sharedPreference =  requireActivity().getSharedPreferences("DOXX", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("SECRET_KEY",secretKey)
        editor.commit()
    }

    private fun validateUserInput():Pair<Boolean,String>{
        val userRequest = getUserRequest()
        return viewModel.validateCredentials(userRequest.email,userRequest.password,userRequest.username,false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}