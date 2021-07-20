package com.devmmurray.firebaseshop.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devmmurray.firebaseshop.R
import com.devmmurray.firebaseshop.databinding.ActivityLoginBinding
import com.devmmurray.firebaseshop.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityRegisterBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        this.hideStatusBar()
    }

    override fun onStart() {
        super.onStart()
        binding.loginText.setOnClickListener { finish() }
    }
}