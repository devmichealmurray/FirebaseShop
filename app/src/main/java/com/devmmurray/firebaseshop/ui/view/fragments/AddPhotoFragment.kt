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
import com.devmmurray.firebaseshop.databinding.FragmentAddPhotoBinding
import com.devmmurray.firebaseshop.ui.loadGlideImage
import com.devmmurray.firebaseshop.ui.makeLongToast
import com.devmmurray.firebaseshop.ui.view.activities.MainActivity
import com.devmmurray.firebaseshop.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class AddPhotoFragment : Fragment() {

    private var binding: FragmentAddPhotoBinding? = null
    private var selectedImg: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPhotoBinding.inflate(inflater, container, false)
        return binding?.root
    }


    override fun onStart() {
        super.onStart()
        binding?.apply {
            selectImageBtn.setOnClickListener { addNewPhoto() }
            uploadImageBtn.setOnClickListener { uploadImage() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


    private fun addNewPhoto() =
        if (!checkPermissions()) requestReadStoragePermissions()
        else startImgPickerForResult
            .launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))

    private fun uploadImage() {
        selectedImg?.let {
            val imageExtension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType((activity as MainActivity).contentResolver.getType(it))

            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "Image " + System.currentTimeMillis() + "." + imageExtension
            )

            sRef.putFile(it)
                .addOnSuccessListener { snapShot ->
                    snapShot.metadata?.reference?.downloadUrl?.addOnSuccessListener { url ->
                            binding?.successErrorTv?.text = "Image Uploaded Successfully!\n$url"
                        }
                        ?.addOnFailureListener {
                            context?.makeLongToast("An Error Occurred While Uploading Your Image")
                        }
                }
                .addOnFailureListener {
                    context?.makeLongToast("An Error Occurred While Uploading Your Image")
                }
        }
    }



    private val startImgPickerForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    try {
                        selectedImg = result.data?.data
                        context?.loadGlideImage(selectedImg, binding?.newPhotoImage!!)
                    } catch (e: IOException) {
                        context?.makeLongToast("Oops! Something went wrong: ${e.message}")
                    }
                }
            }
        }


    private fun checkPermissions() =
        ContextCompat.checkSelfPermission(
            (activity as MainActivity),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestReadStoragePermissions() {
        ActivityCompat.requestPermissions(
            (activity as MainActivity),
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            Constants.READ_STORAGE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addNewPhoto()
            } else {
                context?.makeLongToast("Permission Needed To Access Your Photos")
            }
        }
    }


}