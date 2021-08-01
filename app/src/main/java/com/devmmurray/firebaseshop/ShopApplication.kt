package com.devmmurray.firebaseshop

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.devmmurray.firebaseshop.utils.Constants.SHARED_PREFS

class ShopApplication: Application() {

    companion object {
        var prefs: SharedPreferences? = null
    }

    override fun onCreate() {
        super.onCreate()

        prefs = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
    }
}