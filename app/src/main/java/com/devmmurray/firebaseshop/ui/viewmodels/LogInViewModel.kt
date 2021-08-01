package com.devmmurray.firebaseshop.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devmmurray.firebaseshop.data.api.AuthController
import com.devmmurray.firebaseshop.data.model.LogInForm.logInFormState
import com.devmmurray.firebaseshop.utils.Constants.EMAIL_REGEX
import com.devmmurray.firebaseshop.utils.Constants.PASSWORD_REGEX
import com.devmmurray.firebaseshop.utils.LoginState.*
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LogInViewModel : ViewModel() {

    private val authController = AuthController()

    private fun checkEmail(email: String) = email.matches(EMAIL_REGEX.toRegex())
    private fun checkPassword(password: String) = password.matches(PASSWORD_REGEX.toRegex())

    private val firstName by lazy { MutableLiveData<String>() }
    private val lastName by lazy { MutableLiveData<String>() }
    private val email by lazy { MutableLiveData<String>() }
    private val password by lazy { MutableLiveData<String>() }

    fun isCurrentUserActive() {
        viewModelScope.launch { authController.currentUserActive() }
    }

    fun forgotPassword(email: String) {
        if (email.isNotEmpty() && (checkEmail(email))) {
            viewModelScope.launch { authController.resetPassword(email) }
        } else {
            logInFormState.value = EMAIL_ERROR
        }
    }

    fun logInButtonClick(email: String, password: String) {
        this.email.value = email
        this.password.value = password

        logInFormState.value = when {
            email.isEmpty() || !checkEmail(email) -> EMAIL_ERROR
            password.isEmpty() || !checkPassword(password) -> PASSWORD_ERROR
            else -> LOGGING_IN
        }
    }

    fun registerButtonClick(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        this.firstName.value = firstName
        this.lastName.value = lastName
        this.email.value = email
        this.password.value = password

        logInFormState.value = when {
            firstName.isEmpty() -> FIRST_NAME_ERROR
            lastName.isEmpty() -> LAST_NAME_ERROR
            email.isEmpty() || !checkEmail(email) -> EMAIL_ERROR
            password.isEmpty() || !checkPassword(password) -> PASSWORD_ERROR
            password != confirmPassword -> PASSWORD_MATCH_ERROR
            else -> IS_REGISTERING
        }
    }

    fun signInWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        viewModelScope.launch { authController.logInWithGoogle(credential) }
    }

    fun startLogInProcess() {
        viewModelScope.launch {
            email.value?.let { email ->
                password.value?.let { password ->
                    authController.logInUser(email, password)
                }
            }
        }
    }

    fun startRegistrationProcess() {
        viewModelScope.launch {
            email.value?.let { email ->
                password.value?.let { password ->
                    authController.registerUser(firstName.value, lastName.value, email, password)
                }
            }
        }
    }

    fun getUserInformation() { viewModelScope.launch { authController.getUserInfo() } }
}

