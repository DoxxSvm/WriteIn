package com.doxx.writein

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.doxx.writein.databinding.FragmentLoginBinding
import com.doxx.writein.models.UserRequest
import com.doxx.writein.utils.NetworkResult
import com.doxx.writein.utils.TokenManager
import com.doxx.writein.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {


    private val viewModel by viewModels<AuthViewModel>()
    private var _binding: FragmentLoginBinding?= null
    private val binding get() = _binding!! //so that we don't have to use !! again and again
    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        _binding= FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.btnLogin.setOnClickListener {
            val validationResult = validateUserInput()
            if(validationResult.first){
                viewModel.loginUser(getUserRequest())
            }
            else{
                binding.txtError.text=validationResult.second
            }
        }
        viewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = false
            when(it){
                is NetworkResult.Success ->{
                    tokenManager.saveToken(it.data!!.token)
                    findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
    private fun getUserRequest():UserRequest{
        val email = binding.txtEmail.text.toString()
        val pass = binding.txtPassword.text.toString()
        val secretKey = binding.secretKeyLogin.text.toString()
        saveSecretKey(secretKey)
        return UserRequest(email,pass,"")
    }
    private fun validateUserInput():Pair<Boolean,String>{
        val userRequest = getUserRequest()
        return viewModel.validateCredentials(userRequest.email,userRequest.password,userRequest.username,true)
    }
    private fun saveSecretKey(secretKey: String) {
        val sharedPreference =  requireActivity().getSharedPreferences("DOXX", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("SECRET_KEY",secretKey)
        editor.commit()
    }


}