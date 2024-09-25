package com.on.staccato.presentation.momentcreation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.staccato.databinding.FragmentVisitedAtSelectionBinding
import java.time.LocalDateTime

class VisitedAtSelectionFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentVisitedAtSelectionBinding? = null
    private val binding get() = _binding!!
    private val items = mutableListOf<LocalDateTime>()

    private lateinit var handler: VisitedAtSelectionHandler

    fun setOnVisitedAtSelected(newHandler: VisitedAtSelectionHandler) {
        handler = newHandler
    }

    fun setItems(newItems: List<LocalDateTime>) {
        items.clear()
        items.addAll(newItems)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVisitedAtSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.items = items
        initNumberPicker()
        initConfirmButton()
    }

    private fun initNumberPicker() {
        binding.pickerVisitedAt.apply {
            minValue = 0
            maxValue = (items.size - 1).coerceAtLeast(0)
            wrapSelectorWheel = false
        }
    }

    private fun initConfirmButton() {
        binding.btnVisitedAtConfirm.setOnClickListener {
            handler.onConfirmClicked(items[binding.pickerVisitedAt.value])
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "VisitedAtSelectionModalBottomSheet"
    }
}
