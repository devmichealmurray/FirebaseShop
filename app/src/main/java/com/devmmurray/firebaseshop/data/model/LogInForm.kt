package com.devmmurray.firebaseshop.data.model

import androidx.lifecycle.MutableLiveData
import com.devmmurray.firebaseshop.utils.LoginState

object LogInForm {

    var logInFormState = MutableLiveData<LoginState>()
}