package com.devmmurray.firebaseshop.ui.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.devmmurray.firebaseshop.data.model.LogInForm
import com.devmmurray.firebaseshop.databinding.FragmentRegisterBinding
import com.devmmurray.firebaseshop.ui.gone
import com.devmmurray.firebaseshop.ui.show
import com.devmmurray.firebaseshop.ui.view.activities.MainActivity
import com.devmmurray.firebaseshop.ui.viewmodels.LogInViewModel
import com.devmmurray.firebaseshop.utils.LoginState
import com.devmmurray.firebaseshop.utils.LoginState.*

class RegisterFragment : Fragment() {

    private var binding: FragmentRegisterBinding? = null
    private val viewModel by activityViewModels<LogInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            loginText.setOnClickListener { Navigation.findNavController(it).popBackStack() }
            termsCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    btnRegister.isEnabled = true
                    btnRegister.alpha = 1f
                }
            }
            btnRegister.setOnClickListener { registerClick() }
        }
        LogInForm.logInFormState.observe(viewLifecycleOwner, logInFormObserver)
    }

    private fun registerClick() {
        resetErrors()
        viewModel.registerButtonClick(
            binding?.etFirstName?.text.toString(),
            binding?.etLastName?.text.toString(),
            binding?.etEmail?.text.toString(),
            binding?.etPassword?.text.toString(),
            binding?.etConfirmPassword?.text.toString()
        )
    }

    private val logInFormObserver = Observer<LoginState> { logInState ->
        when (logInState) {
            FIRST_NAME_ERROR -> {
                binding?.firstNameError?.show()
                binding?.etFirstName?.requestFocus()
            }
            LAST_NAME_ERROR -> {
                binding?.lastNameError?.show()
                binding?.etLastName?.requestFocus()
            }
            EMAIL_ERROR -> {
                binding?.emailError?.show()
                binding?.etEmail?.requestFocus()
            }
            PASSWORD_ERROR -> {
                binding?.passwordError?.show()
                binding?.etPassword?.requestFocus()
            }
            PASSWORD_MATCH_ERROR -> {
                binding?.passwordMatchError?.show()
                binding?.etConfirmPassword?.text?.clear()
                binding?.etPassword?.requestFocus()
            }
            SUCCESS -> {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }
        }
    }

    private fun resetErrors() {
        binding?.apply {
            firstNameError.gone()
            lastNameError.gone()
            emailError.gone()
            passwordError.gone()
            passwordMatchError.gone()
        }
    }
}