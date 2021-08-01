package com.devmmurray.firebaseshop.ui.view.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.devmmurray.firebaseshop.R
import com.devmmurray.firebaseshop.data.model.LogInForm
import com.devmmurray.firebaseshop.databinding.FragmentLogInBinding
import com.devmmurray.firebaseshop.ui.hide
import com.devmmurray.firebaseshop.ui.makeLongToast
import com.devmmurray.firebaseshop.ui.show
import com.devmmurray.firebaseshop.ui.view.activities.MainActivity
import com.devmmurray.firebaseshop.ui.viewmodels.LogInViewModel
import com.devmmurray.firebaseshop.utils.Constants.WEB_CLIENT_ID
import com.devmmurray.firebaseshop.utils.LoginState
import com.devmmurray.firebaseshop.utils.LoginState.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

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

        // Check for current user
        viewModel.isCurrentUserActive()

        binding?.apply {
            tvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_logInFragment_to_registerFragment)
            }
            tvForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_logInFragment_to_forgotPasswordDialog)
            }
            btnLogin.setOnClickListener {
                resetErrors()
                viewModel.logInButtonClick(
                    etEmail.text.toString(), etPassword.text.toString()
                )
            }
            btnGoogle.setOnClickListener { launchGoogleSignIn() }
        }
        LogInForm.logInFormState.observe(viewLifecycleOwner, logInFormObserver)
    }


    private fun launchGoogleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireContext(), googleSignInOptions)

        startForResult.launch(googleSignInClient.signInIntent)
    }


    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    account.idToken?.let { viewModel.signInWithGoogle(it) }
                } catch (e: ApiException){
                    context?.makeLongToast(e.message.toString())
                }
            } else {
                context?.makeLongToast("Google Sign In Failed -> ${result.resultCode}")
            }
    }


    private val logInFormObserver = Observer<LoginState> { logInState ->
        when (logInState) {
            EMAIL_ERROR -> {
                binding?.emailError?.show()
                binding?.etEmail?.requestFocus()
            }
            PASSWORD_ERROR -> {
                binding?.passwordError?.show()
                binding?.etPassword?.requestFocus()
            }
            LOGGING_IN -> viewModel.startLogInProcess()
            LOG_IN_ERROR -> context?.makeLongToast("An Error Occurred During Log In. Please Try Again")
            SUCCESS -> {
                viewModel.getUserInformation()
                // TODO: Change log in flow so this is not necessary
                Thread.sleep(1000)
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

