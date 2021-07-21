package com.devmmurray.firebaseshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.devmmurray.firebaseshop.data.model.LogInForm.logInFormState
import com.devmmurray.firebaseshop.utils.Constants.EMAIL_REGEX
import com.devmmurray.firebaseshop.utils.Constants.PASSWORD_REGEX
import com.devmmurray.firebaseshop.utils.LoginState.*

class LogInViewModel : ViewModel() {

    private fun checkEmail(email: String) = email.matches(EMAIL_REGEX.toRegex())
    private fun checkPassword(password: String) = password.matches(PASSWORD_REGEX.toRegex())

    fun logInButtonClick(email: String, password: String) {
        logInFormState.value = when {
            email.isEmpty() || !checkEmail(email) -> EMAIL_ERROR
            password.isEmpty() || !checkPassword(password) -> PASSWORD_ERROR
            else -> SUCCESS
        }
    }

    fun registerButtonClick(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        logInFormState.value = when {
            firstName.isEmpty() -> FIRST_NAME_ERROR
            lastName.isEmpty() -> LAST_NAME_ERROR
            email.isEmpty() || !checkEmail(email) -> EMAIL_ERROR
            password.isEmpty() || !checkPassword(password) -> PASSWORD_ERROR
            password != confirmPassword -> PASSWORD_MATCH_ERROR
            else -> SUCCESS
        }
    }
}

