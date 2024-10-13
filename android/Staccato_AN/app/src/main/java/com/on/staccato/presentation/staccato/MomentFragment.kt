package com.on.staccato.presentation.staccato

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.on.staccato.R
import com.on.staccato.databinding.FragmentMomentBinding
import com.on.staccato.presentation.base.BindingFragment
import com.on.staccato.presentation.common.DeleteDialogFragment
import com.on.staccato.presentation.main.MainActivity
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.staccato.comments.MomentCommentsFragment
import com.on.staccato.presentation.staccato.detail.ViewpagePhotoAdapter
import com.on.staccato.presentation.staccato.feeling.MomentFeelingSelectionFragment
import com.on.staccato.presentation.staccato.viewmodel.MomentViewModel
import com.on.staccato.presentation.util.showToast
import com.on.staccato.presentation.visitupdate.VisitUpdateActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class MomentFragment :
    BindingFragment<FragmentMomentBinding>(R.layout.fragment_moment), MomentToolbarHandler {
    private val momentViewModel: MomentViewModel by viewModels()
    private var momentId by Delegates.notNull<Long>()
    private lateinit var pagePhotoAdapter: ViewpagePhotoAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()
    private val deleteDialog =
        DeleteDialogFragment {
            momentViewModel.deleteMoment(momentId)
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        momentId = arguments?.getLong(STACCATO_ID_KEY) ?: return
        binding.viewModel = momentViewModel
        initToolbarHandler()
        initViewPagerAdapter()
        loadMomentData()
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
        momentViewModel.momentDetail.observe(viewLifecycleOwner) { momentDetail ->
            pagePhotoAdapter.submitList(momentDetail.momentImageUrls)
        }
    }

    private fun loadMomentData() {
        momentViewModel.loadMoment(momentId)
    }

    private fun createChildFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val bundle =
                bundleOf(STACCATO_ID_KEY to momentId)
            val momentFeelingSelectionFragment =
                MomentFeelingSelectionFragment().apply {
                    arguments = bundle
                }
            val momentCommentsFragment = MomentCommentsFragment().apply { arguments = bundle }
            childFragmentManager.beginTransaction()
                .replace(R.id.container_moment_feeling_selection, momentFeelingSelectionFragment)
                .replace(R.id.container_moment_comments, momentCommentsFragment)
                .commit()
        }
    }

    private fun initToolbarHandler() {
        binding.toolbarHandler = this
        binding.toolbarMoment.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeData() {
        momentViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                showToast("스타카토를 불러올 수 없어요!")
                findNavController().popBackStack()
            }
        }
        momentViewModel.isDeleted.observe(viewLifecycleOwner) { isDeleted ->
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
        val momentUpdateLauncher = (activity as MainActivity).staccatoUpdateLauncher
        VisitUpdateActivity.startWithResultLauncher(
            visitId = momentId,
            memoryId = memoryId,
            memoryTitle = memoryTitle,
            context = requireContext(),
            activityLauncher = momentUpdateLauncher,
        )
    }

    companion object {
        const val STACCATO_ID_KEY = "momentId"
        const val DEFAULT_STACCATO_ID = -1L
    }
}
