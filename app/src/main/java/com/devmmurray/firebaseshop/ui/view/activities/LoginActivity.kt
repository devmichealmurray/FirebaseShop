package com.devmmurray.firebaseshop.ui.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devmmurray.firebaseshop.R
import com.devmmurray.firebaseshop.ui.hideStatusBar

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        this.hideStatusBar()
    }
}
