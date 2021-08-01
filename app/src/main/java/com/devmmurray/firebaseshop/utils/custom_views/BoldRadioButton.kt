package com.devmmurray.firebaseshop.utils.custom_views

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class BoldRadioButton(context: Context, attrs: AttributeSet) : AppCompatRadioButton(context, attrs) {

    init { applyFont() }

    private fun applyFont() {
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
        setTypeface(typeface)
    }

}