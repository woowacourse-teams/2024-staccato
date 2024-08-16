package com.woowacourse.staccato.presentation.moment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentVisitBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.common.DeleteDialogFragment
import com.woowacourse.staccato.presentation.common.ToolbarHandler
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_TITLE_KEY
import com.woowacourse.staccato.presentation.moment.adapter.MomentAdapter
import com.woowacourse.staccato.presentation.moment.viewmodel.MomentViewModel
import com.woowacourse.staccato.presentation.moment.viewmodel.MomentViewModelFactory
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visitupdate.VisitUpdateActivity
import kotlin.properties.Delegates

class MomentFragment :
    BindingFragment<FragmentVisitBinding>(R.layout.fragment_visit), ToolbarHandler {
    private val viewModel: MomentViewModel by viewModels { MomentViewModelFactory() }
    private lateinit var momentAdapter: MomentAdapter
    private var momentId by Delegates.notNull<Long>()
    private var memoryId by Delegates.notNull<Long>()
    private var memoryTitle by Delegates.notNull<String>()
    private val deleteDialog =
        DeleteDialogFragment {
            viewModel.deleteMoment(momentId)
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        momentId = arguments?.getLong(MOMENT_ID_KEY) ?: return
        memoryId = arguments?.getLong(MEMORY_ID_KEY) ?: return
        memoryTitle = arguments?.getString(MEMORY_TITLE_KEY) ?: return
        initAdapter()
        initToolbarHandler()
        observeData()
        viewModel.fetchMomentDetailData(momentId)
    }

    private fun initAdapter() {
        momentAdapter = MomentAdapter(mutableListOf())
        binding.rvVisitDetail.adapter = momentAdapter
    }

    private fun initToolbarHandler() {
        binding.toolbarHandler = this
        binding.includeVisitToolbar.toolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeData() {
        viewModel.momentDefault.observe(viewLifecycleOwner) { momentDefault ->
            momentAdapter.updateMomentDefault(momentDefault)
        }
        viewModel.visitLogs.observe(viewLifecycleOwner) { momentLogs ->
            momentAdapter.updateVisitLogs(momentLogs)
        }
        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError) {
                showToast("방문을 불러올 수 없어요!")
                findNavController().popBackStack()
            }
        }
        viewModel.isDeleted.observe(viewLifecycleOwner) { isDeleted ->
            if (isDeleted) findNavController().popBackStack()
        }
    }

    override fun onDeleteClicked() {
        val fragmentManager = parentFragmentManager
        deleteDialog.apply {
            show(fragmentManager, DeleteDialogFragment.TAG)
        }
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
