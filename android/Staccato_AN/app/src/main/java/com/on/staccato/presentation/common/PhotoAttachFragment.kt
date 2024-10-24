package com.on.staccato.presentation.common

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.FragmentPhotoAttachBinding
import com.on.staccato.presentation.staccatocreation.OnUrisSelectedListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class PhotoAttachFragment : BottomSheetDialogFragment(), PhotoAttachHandler {
    private var _binding: FragmentPhotoAttachBinding? = null
    private val binding get() = _binding!!

    private lateinit var uriSelectedListener: OnUrisSelectedListener
    private lateinit var requestGalleryPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private var multipleAbleOption: Boolean = false
    private var currentImageUri: Uri? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initUrisSelectedListener(context)
        initRequestPermissionLauncher()
        initCameraLauncher()
        initGalleryLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPhotoAttachBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.handler = this
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        currentImageUri = null
    }

    override fun onCameraClicked() {
        checkPermissionsAndLaunch(
            permissions = CAMERA_REQUIRED_PERMISSIONS,
            requestPermissionLauncherOnNotGranted = requestCameraPermissionLauncher,
            onGranted = { startCamera() }
        )
    }

    override fun onGalleryClicked() {
        checkPermissionsAndLaunch(
            permissions = arrayOf(GALLERY_REQUIRED_PERMISSION),
            requestPermissionLauncherOnNotGranted = requestGalleryPermissionLauncher,
            onGranted = { launchGallery() },
        )
    }

    fun setMultipleAbleOption(option: Boolean) {
        multipleAbleOption = option
    }

    private fun initUrisSelectedListener(context: Context) {
        if (context is OnUrisSelectedListener) {
            uriSelectedListener = context
        } else {
            throw RuntimeException()
        }
    }

    private fun initRequestPermissionLauncher() {
        requestCameraPermissionLauncher = buildRequestPermissionLauncher { startCamera() }
        requestGalleryPermissionLauncher = buildRequestPermissionLauncher { launchGallery() }
    }

    private fun initCameraLauncher() {
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
                val imageUri = currentImageUri
                if (isSuccess && imageUri != null) {
                    uriSelectedListener.onUrisSelected(imageUri)
                    dismiss()
                }
            }
    }

    private fun initGalleryLauncher() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val imageUris = extractImageUris(result.data)
                    if (imageUris.isNotEmpty()) {
                        uriSelectedListener.onUrisSelected(*imageUris.toTypedArray())
                        dismiss()
                    } else {
                        showGalleryErrorSnackBar()
                    }
                }
            }
    }

    private fun buildRequestPermissionLauncher(
        actionOnPermissionGranted: () -> Unit,
    ): ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { (_, isGranted) -> isGranted }) {
                actionOnPermissionGranted()
            } else {
                showPermissionSnackBar()
            }
        }

    private fun startCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            launchCamera()
        } else {
            showNoCameraSnackBar()
        }
    }

    private fun launchCamera() {
        try {
            currentImageUri = createImageUri()
            currentImageUri?.let { imageUri ->
                cameraLauncher.launch(imageUri)
            }
        } catch (e: ActivityNotFoundException) {
            showCameraErrorSnackBar()
        }
    }

    private fun launchGallery() {
        val intent =
            Intent(Intent.ACTION_PICK)
                .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multipleAbleOption)
        galleryLauncher.launch(intent)
    }

    private fun checkPermissionsAndLaunch(
        permissions: Array<String>,
        requestPermissionLauncherOnNotGranted: ActivityResultLauncher<Array<String>>,
        onGranted: () -> Unit,
    ) {
        val isPermissionGranted =
            permissions.all { permission ->
                ContextCompat.checkSelfPermission(
                    requireContext(), permission,
                ) == PackageManager.PERMISSION_GRANTED
            }
        if (isPermissionGranted) {
            onGranted()
        } else {
            requestPermissionLauncherOnNotGranted.launch(permissions)
        }
    }

    private fun showPermissionSnackBar() {
        showSnackBar(R.string.snack_bar_require_photo_album_permission)
    }

    private fun showCameraErrorSnackBar() {
        showSnackBar(R.string.snack_bar_camera_error)
    }

    private fun showGalleryErrorSnackBar() {
        showSnackBar(R.string.snack_bar_gallery_error)
    }

    private fun showNoCameraSnackBar() {
        showSnackBar(R.string.snack_bar_no_camera)
    }

    private fun showSnackBar(
        @StringRes resId: Int,
    ) {
        val snackBar = makeSnackBar(resId)
        setSnackBarAction(snackBar)
        snackBar.show()
    }

    private fun makeSnackBar(
        @StringRes resId: Int,
    ): Snackbar {
        return Snackbar.make(
            binding.root,
            resId,
            Snackbar.LENGTH_LONG,
        )
    }

    private fun setSnackBarAction(snackBar: Snackbar) {
        snackBar.setAction(R.string.snack_bar_move_to_setting) {
            val uri = Uri.fromParts(PACKAGE_SCHEME, requireContext().packageName, null)
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
            startActivity(intent)
        }
    }

    private fun extractImageUris(intent: Intent?): List<Uri> {
        val imageUris = mutableListOf<Uri>()
        intent?.let {
            it.clipData?.let { clipData ->
                repeat(clipData.itemCount) { index ->
                    imageUris.add(clipData.getItemAt(index).uri)
                }
            } ?: it.data?.let { uri ->
                imageUris.add(uri)
            }
        }
        return imageUris
    }

    private fun createImageUri(): Uri? {
        val timeStamp = SimpleDateFormat(FILENAME_DATE_FORMAT, Locale.KOREA).format(Date())
        val content = createImageContent(timeStamp)
        return requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            content,
        )
    }

    private fun createImageContent(fileName: String): ContentValues =
        ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "img_$fileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        }

    companion object {
        const val TAG = "PhotoAttachModalBottomSheet"
        const val PACKAGE_SCHEME = "package"
        private const val FILENAME_DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val CAMERA_REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
        private val GALLERY_REQUIRED_PERMISSION =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                READ_MEDIA_IMAGES
            } else {
                READ_EXTERNAL_STORAGE
            }
    }
}
