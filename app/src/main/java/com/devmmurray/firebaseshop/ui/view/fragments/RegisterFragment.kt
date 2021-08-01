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
import com.devmmurray.firebaseshop.ui.*
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
        // TODO: Bad flow
        LogInForm.logInFormState.value = null
        binding?.apply {
            loginText.setOnClickListener { Navigation.findNavController(it).popBackStack() }
            termsCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    btnRegister.isEnabled = true
                    btnRegister.alpha = 1f
                    context?.hideKeyboard(view)
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
                binding?.apply {
                    firstNameError.show()
                    etFirstName.requestFocus()
                }
            }
            LAST_NAME_ERROR -> {
                binding?.apply {
                    lastNameError.show()
                    etLastName.requestFocus()
                }
            }
            EMAIL_ERROR -> {
                binding?.apply {
                   emailError.show()
                    etEmail.requestFocus()
                }
            }
            PASSWORD_ERROR -> {
                binding?.apply {
                    passwordError.show()
                    etPassword.requestFocus()
                }
            }
            PASSWORD_MATCH_ERROR -> {
                binding?.apply {
                    passwordMatchError.show()
                    etConfirmPassword.text?.clear()
                    etPassword.requestFocus()
                }
            }
            IS_REGISTERING -> {
                registrationProgress()
                viewModel.startRegistrationProcess()
            }
            REGISTRATION_ERROR -> {
                registrationReset()
                context?.makeLongToast("An Error Occurred During Registration. Please Try Again.")
            }
            SUCCESS -> {
                viewModel.getUserInformation()
                startActivity(Intent(requireContext(), MainActivity::class.java))
                activity?.finish()
            }
        }
    }

    private fun registrationProgress() {
        binding?.apply {
            loginTitleTv.text = "Creating Your Account!"
            loginTitleTv.textSize = 28f
            registrationProgressBar.show()
            tilFirstName.gone()
            tilLastName.gone()
            tilEmail.gone()
            tilPassword.gone()
            tilConfirmPassword.gone()
            termsCheckBox.gone()
            btnRegister.gone()
            logInLayout.gone()

        }
    }

    private fun registrationReset() {
        binding?.apply {
            loginTitleTv.text = "Create An Account"
            loginTitleTv.textSize = 38f
            registrationProgressBar.hide()
            tilFirstName.show()
            tilLastName.show()
            tilEmail.show()
            tilPassword.show()
            tilConfirmPassword.show()
            termsCheckBox.show()
            btnRegister.show()
            logInLayout.show()

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