package com.devmmurray.firebaseshop.data.model

import com.devmmurray.firebaseshop.utils.Constants.USERS
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ShopUser {

    var userId: String? = ""
    private val userDocument
        get() = userId?.let { Firebase.firestore.collection(USERS).document(it) }

    var userEmail: String? = ""
        set(email) {
            userDocument?.set(hashMapOf("email" to email), SetOptions.merge())
            field = email
        }
    var firstName: String? = ""
        set(first) {
                userDocument?.set(hashMapOf("firstName" to first), SetOptions.merge())
            field = first
        }
    var lastName: String? = ""
        set(last) {
                userDocument?.set(hashMapOf("lastName" to last), SetOptions.merge())
            field = last
        }
    var userImage: String? = null
        set(uri) {
                userDocument?.set(hashMapOf("userImage" to uri), SetOptions.merge())
            field = uri
        }
    var phoneNumber: String? = null
        set(number) {
                userDocument?.set(hashMapOf("phoneNumber" to number), SetOptions.merge())
            field = number
        }
    var userGender: String? = ""
        set(gender) {
                userDocument?.set(hashMapOf("gender" to gender), SetOptions.merge())
            field = gender
        }
    var isProfileCompleted: Int? = 0
        set(isCompleted) {
                userDocument?.set(hashMapOf("profileCompleted" to isCompleted), SetOptions.merge())
            field = isCompleted
        }


    fun updateWith(fireStoreData: HashMap<String, Any>) {
        val email = fireStoreData["email"] as? String
        val first = fireStoreData["firstName"] as? String
        val last = fireStoreData["lastName"] as? String
        val gender = fireStoreData["gender"] as? String
        val image = fireStoreData["userImage"] as? String
        val number = fireStoreData["phoneNumber"] as? String
        val profileCompleted = (fireStoreData["profileCompleted"] as? Long)?.toInt()

        email?.let { userEmail = it }
        first?.let { firstName = it }
        last?.let { lastName = it }
        gender?.let { userGender = it }
        image?.let { userImage = it }
        number.let { phoneNumber = it }
        profileCompleted?.let { isProfileCompleted = profileCompleted }
    }

    fun logOut() {
        userId = ""
        userEmail = ""
        firstName = ""
        lastName = ""
        userGender = ""
        userImage = ""
        phoneNumber = null
        isProfileCompleted = null
    }


}