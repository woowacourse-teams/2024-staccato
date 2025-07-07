package com.on.staccato.presentation.categoryselection

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.presentation.databinding.FragmentCategorySelectionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategorySelectionFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentCategorySelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CategorySelectionViewModel
    private lateinit var keyCategory: CategoryCandidate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = (activity as? CategorySelectionViewModelProvider)?.viewModel
            ?: error(MESSAGE_IMPLEMENT_VIEWMODEL_EXCEPTION)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCategorySelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        updateKeyCategory()
        initNumberPicker()
        initConfirmButton()
    }

    private fun updateKeyCategory() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedCategory.collect {
                    it?.let { keyCategory = it }
                }
            }
        }
    }

    private fun initNumberPicker() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectableCategories.collect { candidates ->
                    val items = candidates.categoryCandidates
                    binding.pickerCategorySelection.apply {
                        displayedValues = null
                        minValue = 0
                        maxValue = (items.size - 1).coerceAtLeast(0)
                        displayedValues = items.map { it.categoryTitle }.toTypedArray()
                        wrapSelectorWheel = false
                        setPickerValue(items, keyCategory)
                    }
                }
            }
        }
    }

    private fun NumberPicker.setPickerValue(
        targetList: List<CategoryCandidate>,
        targetKey: CategoryCandidate,
    ) {
        this.value = targetList.indexOf(targetKey).takeIf { it >= 0 } ?: 0
    }

    private fun initConfirmButton() {
        binding.btnCategorySelectionConfirm.setOnClickListener {
            viewModel.selectCategory(binding.pickerCategorySelection.value)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "CategorySelectionModalBottomSheet"
        const val MESSAGE_IMPLEMENT_VIEWMODEL_EXCEPTION =
            "Activity must implement CategorySelectionViewModelProvider"
    }
}
