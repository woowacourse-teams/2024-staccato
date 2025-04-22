package com.on.staccato.presentation.common.color

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.staccato.databinding.FragmentColorSelectionBinding
import com.on.staccato.presentation.common.color.recyclerview.ColorSelectionAdapter
import com.on.staccato.presentation.common.color.recyclerview.ColorSelectionHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ColorSelectionDialogFragment : ColorSelectionHandler, BottomSheetDialogFragment() {
    private var _binding: FragmentColorSelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ColorSelectionViewModel by viewModels()
    private lateinit var colorSelectionAdapter: ColorSelectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selectedColor =
            requireArguments().getString(SELECTED_COLOR_LABEL)?.let(CategoryColor::getColorBy)
        if (selectedColor != null) {
            viewModel.selectCategoryColor(selectedColor)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentColorSelectionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        initAdapter()
        initConfirmButtonListener()
        observeColorSelectionEvents()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onColorSelected(color: CategoryColor) {
        viewModel.selectCategoryColor(color)
    }

    private fun initAdapter() {
        colorSelectionAdapter =
            ColorSelectionAdapter(
                CategoryColor.getAllColors(),
                this,
            )
        binding.rvStaccatoColorSelection.adapter = colorSelectionAdapter
    }

    private fun initConfirmButtonListener() {
        binding.btnColorSelectionConfirm.setOnClickListener {
            val result = bundleOf(SELECTED_COLOR_LABEL to viewModel.selectedColor.value?.label)
            parentFragmentManager.setFragmentResult(COLOR_SELECTION_REQUEST_KEY, result)
            dismiss()
        }
    }

    private fun observeColorSelectionEvents() {
        viewModel.selectedColor.observe(viewLifecycleOwner) { selectedColor ->
            colorSelectionAdapter.changeSelectedItem(selectedColor)
        }
    }

    companion object {
        const val TAG = "ColorSelectDialogFragment"
        const val COLOR_SELECTION_REQUEST_KEY = "color_result"
        const val SELECTED_COLOR_LABEL = "SelectedColorLabel"

        fun newInstance(selectedColor: CategoryColor): ColorSelectionDialogFragment {
            return ColorSelectionDialogFragment().apply {
                arguments =
                    Bundle().apply {
                        putString(SELECTED_COLOR_LABEL, selectedColor.label)
                    }
            }
        }
    }
}
