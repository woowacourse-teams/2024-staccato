package com.on.staccato.presentation.common

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Activity.RESULT_OK
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
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.FragmentPhotoAttachBinding
import com.on.staccato.presentation.momentcreation.OnUrisSelectedListener
import com.on.staccato.presentation.util.showToast

class PhotoAttachFragment : BottomSheetDialogFragment(), PhotoAttachHandler {
    private var _binding: FragmentPhotoAttachBinding? = null
    private val binding get() = _binding!!

    private lateinit var uriSelectedListener: OnUrisSelectedListener
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var multipleAbleOption: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initUrisSelectedListener(context)
        initRequestPermissionLauncher()
        initGalleryLauncher()
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
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermissionGranted ->
                if (isPermissionGranted) {
                    launchGallery()
                } else {
                    showPermissionSnackBar()
                }
            }
    }

    private fun launchGallery() {
        val intent =
            Intent(Intent.ACTION_PICK)
                .setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multipleAbleOption)
        galleryLauncher.launch(intent)
    }

    private fun showPermissionSnackBar() {
        val snackBar = makeSnackBar()
        setSnackBarAction(snackBar)
        snackBar.show()
    }

    private fun makeSnackBar(): Snackbar {
        return Snackbar.make(
            binding.root,
            R.string.snack_bar_require_photo_album_permission,
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

    private fun initGalleryLauncher() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val imageUris = extractImageUris(result.data)
                    if (imageUris.isNotEmpty()) {
                        uriSelectedListener.onUrisSelected(*imageUris.toTypedArray())
                        dismiss()
                    } else {
                        showToast("사진을 불러올 수 없습니다.")
                    }
                }
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
    }

    override fun onCameraClicked() {
        showToast(getString(R.string.all_default_not_supported))
    }

    override fun onGalleryClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissionsAndLaunch(READ_MEDIA_IMAGES)
        } else {
            checkPermissionsAndLaunch(READ_EXTERNAL_STORAGE)
        }
    }

    private fun checkPermissionsAndLaunch(permission: String) {
        val isPermissionGranted =
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission,
            ) == PackageManager.PERMISSION_GRANTED
        if (isPermissionGranted) {
            launchGallery()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    companion object {
        const val TAG = "PhotoAttachModalBottomSheet"
        const val PACKAGE_SCHEME = "package"
    }
}
