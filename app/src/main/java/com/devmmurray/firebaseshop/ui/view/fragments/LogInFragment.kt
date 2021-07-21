package com.devmmurray.firebaseshop.ui.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.devmmurray.firebaseshop.R
import com.devmmurray.firebaseshop.data.model.LogInForm
import com.devmmurray.firebaseshop.databinding.FragmentLogInBinding
import com.devmmurray.firebaseshop.ui.hide
import com.devmmurray.firebaseshop.ui.show
import com.devmmurray.firebaseshop.ui.view.activities.MainActivity
import com.devmmurray.firebaseshop.ui.viewmodels.LogInViewModel
import com.devmmurray.firebaseshop.utils.LoginState

class LogInFragment : Fragment() {

    private var binding: FragmentLogInBinding? = null
    private val viewModel by activityViewModels<LogInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            tvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_logInFragment_to_registerFragment)
            }
            btnLogin.setOnClickListener {
                resetErrors()
                viewModel.logInButtonClick(
                    etEmail.text.toString(), etPassword.text.toString()
                )
            }
        }

        LogInForm.logInFormState.observe(viewLifecycleOwner, logInFormObserver)
    }

    private val logInFormObserver = Observer<LoginState> { logInState ->
        when (logInState) {
            LoginState.EMAIL_ERROR -> {
                binding?.emailError?.show()
                binding?.etEmail?.requestFocus()
            }
            LoginState.PASSWORD_ERROR -> {
                binding?.passwordError?.show()
                binding?.etPassword?.requestFocus()
            }
            LoginState.SUCCESS -> {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }
        }
    }

    private fun resetErrors() {
        binding?.apply {
            emailError.hide()
            passwordError.hide()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}

