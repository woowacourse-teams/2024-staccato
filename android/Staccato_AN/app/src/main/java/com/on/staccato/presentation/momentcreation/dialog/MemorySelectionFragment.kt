package com.on.staccato.presentation.momentcreation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.staccato.databinding.FragmentMemorySelectionBinding
import com.on.staccato.domain.model.MemoryCandidate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemorySelectionFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentMemorySelectionBinding? = null
    private val binding get() = _binding!!
    private val items = mutableListOf<MemoryCandidate>()
    private lateinit var handler: MemorySelectionHandler

    fun setOnMemorySelected(newHandler: MemorySelectionHandler) {
        handler = newHandler
    }

    fun setItems(newItems: List<MemoryCandidate>) {
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
            displayedValues = items.map { it.memoryTitle }.toTypedArray()
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
