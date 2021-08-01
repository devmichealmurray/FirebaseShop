package com.devmmurray.firebaseshop.utils

object Constants {

    const val EMAIL_REGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}\$"
    const val PASSWORD_REGEX = "^(?=.*[0-9a-zA-Z]).{6,64}\$"
    const val WEB_CLIENT_ID = "569552727941-3i1ejmm3pfch0ha2mvgauc2d1verncno.apps.googleusercontent.com"

    const val SHARED_PREFS = "FireBaseShopPrefs"
    const val LOGGED_IN_USER = "LoggedInUser"

    const val USERS = "users"
    const val MALE = "MALE"
    const val FEMALE = "FEMALE"

    const val READ_STORAGE_PERMISSION = 1234
    const val IMG_PICKER_REQUEST_CODE = 2345

}