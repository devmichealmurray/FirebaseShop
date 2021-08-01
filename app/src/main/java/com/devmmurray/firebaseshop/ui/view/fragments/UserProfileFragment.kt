package com.devmmurray.firebaseshop.ui.view.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.devmmurray.firebaseshop.R
import com.devmmurray.firebaseshop.data.model.LogInForm.logInFormState
import com.devmmurray.firebaseshop.data.model.ShopUser
import com.devmmurray.firebaseshop.databinding.FragmentUserProfileBinding
import com.devmmurray.firebaseshop.ui.loadGlideImage
import com.devmmurray.firebaseshop.ui.makeLongToast
import com.devmmurray.firebaseshop.ui.show
import com.devmmurray.firebaseshop.ui.view.activities.MainActivity
import com.devmmurray.firebaseshop.utils.Constants.FEMALE
import com.devmmurray.firebaseshop.utils.Constants.MALE
import com.devmmurray.firebaseshop.utils.Constants.READ_STORAGE_PERMISSION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException


class UserProfileFragment : Fragment() {

    private var binding: FragmentUserProfileBinding? = null
    private var selectedImg: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return (binding?.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUi()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setUpUi() {
        binding?.apply {
            etUserFirstName.setText(ShopUser.firstName)
            etUserLastName.setText(ShopUser.lastName)
            etUserEmail.setText(ShopUser.userEmail)
            context?.loadGlideImage(Uri.parse(ShopUser.userImage), binding?.userProfileImage!!)
            etUserPhoneNumber.setText(ShopUser.phoneNumber)

            when (ShopUser.userGender) {
                MALE -> maleRadioBtn.isChecked = true
                FEMALE -> femaleRadioBtn.isChecked = true
            }

            logOutText.setOnClickListener { logUserOut() }
            logOutIcon.setOnClickListener { logUserOut() }
            addPhotoFab.setOnClickListener { addProfilePhoto() }
            saveBtn.setOnClickListener { validateNewData() }
            settingsBtn.setOnClickListener { navigateToSettings() }

        }
    }


    /**
     * Click Listener Functions
     */
    private fun logUserOut() {
        ShopUser.logOut()
        logInFormState.value = null
        FirebaseAuth.getInstance().signOut()
        if (activity is MainActivity) (activity as MainActivity).navigateToLogin()
    }

    private fun addProfilePhoto() = if (!checkPermissions()) requestReadStoragePermissions()
    else startImgPickerForResult
        .launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))

    private fun navigateToSettings() {
            findNavController().navigate(R.id.action_userProfileFragment_to_settingsFragment)
    }


    /**
     * Profile Image
     */
    private val startImgPickerForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    try {
                        selectedImg = result.data?.data
                        context?.loadGlideImage(selectedImg, binding?.userProfileImage!!)
                        saveUserProfileImage()
                    } catch (e: IOException) {
                        context?.makeLongToast("Oops! Something went wrong: ${e.message}")
                    }
                }
            }
        }

    private fun saveUserProfileImage() {
        selectedImg?.let {
            val imageExtension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType((activity as MainActivity).contentResolver.getType(it))

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "Image " + System.currentTimeMillis() + "." + imageExtension
            )

            sRef.putFile(it)
                .addOnSuccessListener { snapShot ->
                    snapShot.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->
                       ShopUser.userImage = url.toString()
                    }
                        ?.addOnFailureListener {
                            context?.makeLongToast("An Error Occurred While Uploading Your Image")
                        }
                }
                .addOnFailureListener {
                    context?.makeLongToast("An Error Occurred While Saving Your Image")
                }
        }
    }

    /**
     *  Save New Data
     */
    private fun validateNewData() {
        when {
            binding?.etUserFirstName?.text.toString() == "" -> {
                binding?.firstNameUpdateMsg?.apply {
                    text = "First Name Can Not Be Left Blank"
                    show()
                }
            }
            binding?.etUserLastName?.text.toString() == "" -> {
                binding?.lastNameUpdateMsg?.apply {
                    text = "Last Name Can Not Be Left Blank"
                    show()
                }
            }
            binding?.etUserEmail?.text.toString() == "" -> {
                binding?.emailUpdateMsg?.apply {
                    text = "Email Can Not Be Left Blank"
                    show()
                }
            }
            binding?.etUserPhoneNumber?.text.toString() == "" -> {
                binding?.numberUpdateMsg?.apply {
                    text = "Phone Number Can Not Be Left Blank"
                    show()
                }
            }
            else -> {
                if (ShopUser.firstName != binding?.etUserFirstName?.text.toString())
                    ShopUser.firstName = binding?.etUserFirstName?.text.toString()

                if (ShopUser.lastName != binding?.etUserLastName?.text.toString())
                    ShopUser.lastName = binding?.etUserLastName?.text.toString()

                if (ShopUser.userEmail != binding?.etUserEmail?.text.toString())
                    ShopUser.userEmail = binding?.etUserEmail?.text.toString()

                if (ShopUser.phoneNumber != binding?.etUserPhoneNumber?.text.toString())
                    ShopUser.phoneNumber = binding?.etUserPhoneNumber?.text.toString()

                ShopUser.isProfileCompleted = 1

                ShopUser.userGender = if (binding?.maleRadioBtn?.isChecked == true) MALE else FEMALE
            }

        }
    }


    /**
     *  Permissions
     */
    private fun checkPermissions() =
        ContextCompat.checkSelfPermission(
            (activity as MainActivity),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestReadStoragePermissions() {
        ActivityCompat.requestPermissions(
            (activity as MainActivity),
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_STORAGE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addProfilePhoto()
            } else {
                context?.makeLongToast("Permission Needed To Access Your Photos")
            }
        }
    }

}