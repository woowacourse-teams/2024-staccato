package com.woowacourse.staccato.presentation.visitcreation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woowacourse.staccato.databinding.FragmentTravelSelectionBinding
import com.woowacourse.staccato.presentation.visitcreation.model.VisitTravelUiModel

class TravelSelectionFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentTravelSelectionBinding? = null
    private val binding get() = _binding!!
    private val items = mutableListOf<VisitTravelUiModel>()

    private lateinit var handler: TravelSelectionHandler

    fun setOnTravelSelected(newHandler: TravelSelectionHandler) {
        handler = newHandler
    }

    fun setItems(newItems: List<VisitTravelUiModel>) {
        items.clear()
        items.addAll(newItems)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTravelSelectionBinding.inflate(inflater, container, false)
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
        binding.pickerTravelSelection.apply {
            minValue = 0
            maxValue = (items.size - 1).coerceAtLeast(0)
            displayedValues = items.map { it.title }.toTypedArray()
            wrapSelectorWheel = false
        }
    }

    private fun initConfirmButton() {
        binding.btnTravelSelectionConfirm.setOnClickListener {
            handler.onConfirmClicked(items[binding.pickerTravelSelection.value])
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "TravelSelectionModalBottomSheet"
    }
}
