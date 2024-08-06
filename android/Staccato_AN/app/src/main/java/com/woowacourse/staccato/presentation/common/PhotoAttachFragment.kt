package com.woowacourse.staccato.presentation.common

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentPhotoAttachBinding

class PhotoAttachFragment : BottomSheetDialogFragment(), PhotoAttachHandler {
    private var _binding: FragmentPhotoAttachBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initRequestPermissionLauncher()
    }

    private fun initRequestPermissionLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermissionGranted ->
                if (isPermissionGranted) {
                    // 갤러리 열기
                } else {
                    showPermissionSnackBar()
                }
            }
    }

    private fun showPermissionSnackBar() {
        val snackBar =
            Snackbar.make(
                binding.root,
                R.string.snack_bar_require_photo_album_permission,
                Snackbar.LENGTH_LONG,
            )
        snackBar.setAction(R.string.snack_bar_move_to_setting) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts(SCHEME, requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        snackBar.show()
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
        // 카메라 열기
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
            // 갤러리 열기
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    companion object {
        const val TAG = "PhotoAttachModalBottomSheet"
        const val SCHEME = "package"
    }
}
