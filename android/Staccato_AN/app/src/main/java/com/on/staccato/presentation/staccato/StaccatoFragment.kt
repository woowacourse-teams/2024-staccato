package com.on.staccato.presentation.staccato

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.on.staccato.R
import com.on.staccato.databinding.FragmentStaccatoBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.common.DeleteDialogFragment
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.staccato.comments.StaccatoCommentsFragment
import com.on.staccato.presentation.staccato.detail.ViewpagePhotoAdapter
import com.on.staccato.presentation.staccato.feeling.StaccatoFeelingSelectionFragment
import com.on.staccato.presentation.staccato.viewmodel.StaccatoViewModel
import com.on.staccato.presentation.staccatoupdate.StaccatoUpdateActivity
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class StaccatoFragment :
    BindingFragment<FragmentStaccatoBinding>(R.layout.fragment_staccato), StaccatoToolbarHandler {
    private val staccatoViewModel: StaccatoViewModel by viewModels()
    private var staccatoId by Delegates.notNull<Long>()
    private lateinit var pagePhotoAdapter: ViewpagePhotoAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private val deleteDialog =
        DeleteDialogFragment {
            staccatoViewModel.deleteStaccato(staccatoId)
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        staccatoId = arguments?.getLong(STACCATO_ID_KEY) ?: return
        binding.viewModel = staccatoViewModel
        initToolbarHandler()
        initViewPagerAdapter()
        loadStaccatoData()
        observeData()
        createChildFragments(savedInstanceState)
        observeViewModel()
    }

    private fun initViewPagerAdapter() {
        pagePhotoAdapter = ViewpagePhotoAdapter()
        binding.vpPhotoHorizontal.adapter = pagePhotoAdapter
        TabLayoutMediator(
            binding.tabPhotoHorizontal,
            binding.vpPhotoHorizontal,
        ) { _, _ -> }.attach()
    }

    private fun observeViewModel() {
        staccatoViewModel.staccatoDetail.observe(viewLifecycleOwner) { staccatoDetail ->
            pagePhotoAdapter.submitList(staccatoDetail.staccatoImageUrls)
        }
    }

    private fun loadStaccatoData() {
        staccatoViewModel.loadStaccato(staccatoId)
    }

    private fun createChildFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val bundle =
                bundleOf(STACCATO_ID_KEY to staccatoId)
            val staccatoFeelingSelectionFragment =
                StaccatoFeelingSelectionFragment().apply {
                    arguments = bundle
                }
            val staccatoCommentsFragment = StaccatoCommentsFragment().apply { arguments = bundle }
            childFragmentManager.beginTransaction()
                .replace(R.id.container_staccato_feeling_selection, staccatoFeelingSelectionFragment)
                .replace(R.id.container_staccato_comments, staccatoCommentsFragment)
                .commit()
        }
    }

    private fun initToolbarHandler() {
        binding.toolbarHandler = this
        binding.toolbarStaccato.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeData() {
        staccatoViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                showToast("스타카토를 불러올 수 없어요!")
                findNavController().popBackStack()
            }
        }
        staccatoViewModel.isDeleted.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) {
                sharedViewModel.setStaccatosHasUpdated()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDeleteClicked() {
        deleteDialog.show(parentFragmentManager, DeleteDialogFragment.TAG)
    }

    override fun onUpdateClicked(
        memoryId: Long,
        memoryTitle: String,
    ) {
        val staccatoUpdateLauncher = (activity as MainActivity).staccatoUpdateLauncher
        StaccatoUpdateActivity.startWithResultLauncher(
            staccatoId = staccatoId,
            memoryId = memoryId,
            memoryTitle = memoryTitle,
            context = requireContext(),
            activityLauncher = staccatoUpdateLauncher,
        )
    }

    companion object {
        const val STACCATO_ID_KEY = "staccatoId"
        const val DEFAULT_STACCATO_ID = -1L
    }
}
