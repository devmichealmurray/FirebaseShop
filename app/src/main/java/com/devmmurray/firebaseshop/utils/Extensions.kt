package com.devmmurray.firebaseshop.ui

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.devmmurray.firebaseshop.R
import kotlin.math.min

// ----------- Activity -----------------//
// Lazy delegate that inflates the binding of Activities
inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) = lazy(LazyThreadSafetyMode.NONE) {
    bindingInflater.invoke(layoutInflater)
}

fun Activity.hideStatusBar() {
    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.hide(WindowInsets.Type.statusBars())
    } else {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}



// ---------- Dialog Fragment -----------//
// Helper to set width of dialog fragment

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun DialogFragment.setWidthPercentage(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val displayMetrics = Resources.getSystem().displayMetrics
    val rectangle = displayMetrics.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = min(450.px, (rectangle.width() * percent).toInt())
    dialog?.window?.setLayout(percentWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
}



// ---------- Context -----------//
fun Context.makeLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.loadGlideImage(imageUri: Uri?, imageView: ImageView) {
    try {
        Glide
            .with(this)
            .load(imageUri)
            .transform(CenterCrop(), RoundedCorners(14))
            .placeholder(R.drawable.img_no_user)
            .into(imageView)
    } catch (e: Exception) {
        this.makeLongToast("Error Loading Image: ${e.localizedMessage}")
    }
}



// ---------- VIEW --------- //
fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}