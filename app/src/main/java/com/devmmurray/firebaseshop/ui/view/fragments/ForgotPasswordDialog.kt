package com.devmmurray.firebaseshop.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.devmmurray.firebaseshop.data.model.LogInForm
import com.devmmurray.firebaseshop.databinding.DialogFragmentForgotPasswordBinding
import com.devmmurray.firebaseshop.ui.*
import com.devmmurray.firebaseshop.ui.viewmodels.LogInViewModel
import com.devmmurray.firebaseshop.utils.LoginState

class ForgotPasswordDialog : DialogFragment() {

    private var binding: DialogFragmentForgotPasswordBinding? = null
    private val viewModel by activityViewModels<LogInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentForgotPasswordBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogInForm.logInFormState.value = null
        setWidthPercentage(90)

        binding?.apply {
            dialogClose.setOnClickListener { dismiss() }
            btnPasswordSubmit.setOnClickListener {
                emailError.hide()
                context?.hideKeyboard(view)
                etEmail.text?.toString()?.let { viewModel.forgotPassword(it) }
            }
        }
        LogInForm.logInFormState.observe(viewLifecycleOwner, logInFormObserver)
    }

    private val logInFormObserver = Observer<LoginState> { loginState ->
        when (loginState) {
            LoginState.EMAIL_ERROR -> {
                binding?.apply {
                    emailError.show()
                    etEmail.text?.clear()
                    etEmail.requestFocus()
                }
            }
            LoginState.PASSWORD_RESET_ERROR -> {
                binding?.apply {
                    // TODO: Should show an error that email doesn't exist
                    emailError.text = "An Unknown Server Error Occurred. Please Try Again."
                    emailError.show()
                    etEmail.text?.clear()
                    etEmail.requestFocus()
                }
            }
            LoginState.PASSWORD_RESET_SUCCESS -> {
                binding?.apply {
                    tilEmail.hide()
                    btnPasswordSubmit.gone()
                    passwordResetSuccess.show()
                }
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        LogInForm.logInFormState.value = null
        binding = null
    }
}