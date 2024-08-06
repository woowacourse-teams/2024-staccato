package com.woowacourse.staccato.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woowacourse.staccato.databinding.FragmentPhotoAttachBinding

class PhotoAttachFragment : BottomSheetDialogFragment(), PhotoAttachHandler {
    private var _binding: FragmentPhotoAttachBinding? = null
    private val binding get() = _binding!!

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
        // 갤러리 열기
    }

    companion object {
        const val TAG = "PhotoAttachModalBottomSheet"
    }
}
