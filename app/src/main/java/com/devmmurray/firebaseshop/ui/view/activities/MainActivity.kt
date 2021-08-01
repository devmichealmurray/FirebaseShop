package com.devmmurray.firebaseshop.ui.view.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.devmmurray.firebaseshop.R
import com.devmmurray.firebaseshop.databinding.ActivityMainBinding
import com.devmmurray.firebaseshop.ui.makeLongToast
import com.devmmurray.firebaseshop.ui.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private var doubleBackPressedOnce: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setUpNavigation()
    }

    fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onBackPressed() { doubleBackToExit() }

    @Suppress("DEPRECATION")
    fun doubleBackToExit() {
        if (doubleBackPressedOnce) {
            super.onBackPressed()
            return
        }
        doubleBackPressedOnce = true
        this.makeLongToast("Press Back Again To Exit")

        Handler().postDelayed({ doubleBackPressedOnce = false}, 1500)
    }

    private fun setUpNavigation() {
        val navView: BottomNavigationView = binding.mainActivityBottomNav
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(navView, navHostFragment.navController)
    }

}