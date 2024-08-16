package com.woowacourse.staccato.presentation.visitcreation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woowacourse.staccato.databinding.FragmentMemorySelectionBinding
import com.woowacourse.staccato.presentation.visitcreation.model.MomentMemoryUiModel

class MemorySelectionFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentMemorySelectionBinding? = null
    private val binding get() = _binding!!
    private val items = mutableListOf<MomentMemoryUiModel>()

    private lateinit var handler: MemorySelectionHandler

    fun setOnMemorySelected(newHandler: MemorySelectionHandler) {
        handler = newHandler
    }

    fun setItems(newItems: List<MomentMemoryUiModel>) {
        items.clear()
        items.addAll(newItems)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMemorySelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        initNumberPicker()
        initConfirmButton()
    }

    private fun initNumberPicker() {
        binding.pickerMemorySelection.apply {
            minValue = 0
            maxValue = (items.size - 1).coerceAtLeast(0)
            displayedValues = items.map { it.title }.toTypedArray()
            wrapSelectorWheel = false
        }
    }

    private fun initConfirmButton() {
        binding.btnMemorySelectionConfirm.setOnClickListener {
            handler.onConfirmClicked(items[binding.pickerMemorySelection.value])
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MemorySelectionModalBottomSheet"
    }
}
