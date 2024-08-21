package com.woowacourse.staccato.presentation.moment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentMomentBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.common.DeleteDialogFragment
import com.woowacourse.staccato.presentation.common.ToolbarHandler
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_TITLE_KEY
import com.woowacourse.staccato.presentation.moment.comments.MomentCommentsFragment
import com.woowacourse.staccato.presentation.moment.detail.MomentDetailFragment
import com.woowacourse.staccato.presentation.moment.feeling.MomentFeelingSelectionFragment
import com.woowacourse.staccato.presentation.moment.viewmodel.MomentViewModel
import com.woowacourse.staccato.presentation.moment.viewmodel.MomentViewModelFactory
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visitupdate.VisitUpdateActivity
import kotlin.properties.Delegates

class MomentFragment :
    BindingFragment<FragmentMomentBinding>(R.layout.fragment_moment), ToolbarHandler {
    private val momentViewModel: MomentViewModel by viewModels { MomentViewModelFactory() }
    private var momentId by Delegates.notNull<Long>()
    private var memoryId by Delegates.notNull<Long>()
    private var memoryTitle by Delegates.notNull<String>()
    private val deleteDialog =
        DeleteDialogFragment {
            momentViewModel.deleteMoment(momentId)
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        momentId = arguments?.getLong(MOMENT_ID_KEY) ?: return
        memoryId = arguments?.getLong(MEMORY_ID_KEY) ?: return
        memoryTitle = arguments?.getString(MEMORY_TITLE_KEY) ?: return
        initToolbarHandler()
        loadMomentData()
        observeData()
        createChildFragments(savedInstanceState)
    }

    private fun loadMomentData() {
        momentViewModel.loadMoment(momentId)
    }

    private fun createChildFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val bundle =
                bundleOf(MOMENT_ID_KEY to momentId)
            val momentFeelingSelectionFragment =
                MomentFeelingSelectionFragment().apply {
                    arguments = bundle
                }
            val momentCommentsFragment = MomentCommentsFragment().apply { arguments = bundle }
            childFragmentManager.beginTransaction()
                .replace(R.id.container_moment_detail, MomentDetailFragment())
                .replace(R.id.container_moment_feeling_selection, momentFeelingSelectionFragment)
                .replace(R.id.container_moment_comments, momentCommentsFragment)
                .commit()
        }
    }

    private fun initToolbarHandler() {
        binding.toolbarHandler = this
        binding.includeMomentToolbar.toolbarDetail.setNavigationOnClickListener {
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
            if (isDeleted) findNavController().popBackStack()
        }
    }

    override fun onDeleteClicked() {
        deleteDialog.show(parentFragmentManager, DeleteDialogFragment.TAG)
    }

    override fun onUpdateClicked() {
        val momentUpdateLauncher = (activity as MainActivity).visitUpdateLauncher
        VisitUpdateActivity.startWithResultLauncher(
            visitId = momentId,
            memoryId = memoryId,
            memoryTitle = memoryTitle,
            context = requireContext(),
            activityLauncher = momentUpdateLauncher,
        )
    }

    companion object {
        const val MOMENT_ID_KEY = "momentId"
    }
}
