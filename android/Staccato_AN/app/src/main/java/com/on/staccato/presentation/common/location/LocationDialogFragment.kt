package com.on.staccato.presentation.common.location

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.on.staccato.databinding.FragmentLocationDialogBinding
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationDialogFragment : DialogFragment(), LocationDialogHandler {
    private var _binding: FragmentLocationDialogBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel by activityViewModels<SharedViewModel>()

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return
        val context = context ?: return

        val width = context.resources.displayMetrics.widthPixels
        window.setLayout((width * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLocationDialogBinding.inflate(inflater, container, false)
        binding.handler = this
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCancelClicked() {
        sharedViewModel.updateIsPermissionCancelClicked()
        dismiss()
    }

    override fun onSettingClicked() {
        sharedViewModel.updateIsSettingClicked(isSettingClicked = true)
        navigateToSetting()
        dismiss()
    }

    private fun navigateToSetting() {
        val uri = Uri.fromParts(PACKAGE_SCHEME, context?.packageName, null)
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(uri)
        startActivity(intent)
    }

    companion object {
        const val TAG = "LocationDialogFragment"
        const val PACKAGE_SCHEME = "package"
        const val PERMISSION_CANCEL_KEY = "isPermissionCanceled"
    }
}
