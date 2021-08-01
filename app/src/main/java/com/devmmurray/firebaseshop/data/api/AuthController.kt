package com.devmmurray.firebaseshop.data.api

import com.devmmurray.firebaseshop.data.model.LogInForm.logInFormState
import com.devmmurray.firebaseshop.data.model.ShopUser
import com.devmmurray.firebaseshop.utils.Constants.USERS
import com.devmmurray.firebaseshop.utils.LoginState.*
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AuthController {

    private val auth = Firebase.auth
    private val fireStore = Firebase.firestore
    private val docRef
        get() = ShopUser.userId?.let { fireStore.collection(USERS).document(it) }

    suspend fun currentUserActive() = withContext(Dispatchers.IO) {
        if (auth.currentUser != null && auth.currentUser!!.email != null) {
            auth.currentUser?.uid?.let { ShopUser.userId = it }
            auth.currentUser?.email?.let { ShopUser.userEmail = it }
            logInFormState.postValue(SUCCESS)
        }
    }

    // ----------- REGISTRATION AND SAVING NEW USER -------------- //
    suspend fun registerUser(
        firstName: String?,
        lastName: String?,
        email: String,
        password: String
    ) = withContext(Dispatchers.IO) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    val user = result.result?.user
                    user?.let {
                        ShopUser.userId = user.uid
                        ShopUser.userEmail = user.email
                        ShopUser.firstName = firstName
                        ShopUser.lastName = lastName
                    }
                    saveNewUser(firstName, lastName, email)
                    logInFormState.postValue(SUCCESS)
                } else {
                    logInFormState.postValue(REGISTRATION_ERROR)
                    return@addOnCompleteListener
                }
            }
    }

    private fun saveNewUser(firstName: String?, lastName: String?, email: String) {
        val df: DateFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm'Z'",
            Locale.getDefault()
        )
        df.timeZone = TimeZone.getTimeZone("UTC")
        val nowAsISO: String = df.format(Date())
        val data = hashMapOf(
            "createdAt" to nowAsISO,
            "platform" to "android",
            "userId" to "",
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "userImage" to "",
            "phoneNumber" to 0,
            "gender" to "",
            "profileCompleted" to 0
        )
        docRef?.set(data, SetOptions.merge())
    }

    // ----------- LOG IN AND AUTHENTICATION METHODS -------------- //
    suspend fun logInUser(email: String, password: String) =
        withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val user = result.result?.user
                        user?.let {
                            ShopUser.userId = user.uid
                            ShopUser.userEmail = user.email
                        }
                        logInFormState.postValue(SUCCESS)
                    } else {
                        logInFormState.postValue(LOG_IN_ERROR)
                    }
                }
        }

    suspend fun logInWithGoogle(credential: AuthCredential) =
        withContext(Dispatchers.IO) {
            auth.signInWithCredential(credential)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        val user = result.result?.user
                        user?.let {
                            val firstName = user.displayName?.indexOf(" ")?.let { space ->
                                user.displayName?.substring(0, space)
                            }
                            ShopUser.userId = user.uid
                            ShopUser.userEmail = user.email
                            ShopUser.firstName = firstName
                        }
                        logInFormState.postValue(SUCCESS)
                    } else {
                        logInFormState.postValue(LOG_IN_ERROR)
                    }
                }
        }

    suspend fun resetPassword(email: String) =
        withContext(Dispatchers.IO) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { result ->
                    logInFormState.postValue(
                        if (result.isSuccessful) PASSWORD_RESET_SUCCESS else PASSWORD_RESET_ERROR
                    )
                }
        }

    suspend fun getUserInfo() =
        withContext(Dispatchers.IO) {
            val getUserData = docRef?.get()
            getUserData?.addOnSuccessListener { document ->
                if (document != null && document.data != null) {
                    ShopUser.updateWith(document.data as HashMap<String, Any>)
                }
            }
        }

}
