package com.devmmurray.firebaseshop.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devmmurray.firebaseshop.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityLoginBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        this.hideStatusBar()
    }

    override fun onStart() {
        super.onStart()
        binding.tvRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }
}