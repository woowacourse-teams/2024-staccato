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
import com.woowacourse.staccato.presentation.moment.viewmodel.VisitViewModel
import com.woowacourse.staccato.presentation.moment.viewmodel.VisitViewModelFactory
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visitupdate.VisitUpdateActivity
import kotlin.properties.Delegates

class VisitFragment :
    BindingFragment<FragmentVisitBinding>(R.layout.fragment_visit), ToolbarHandler {
    private val viewModel: VisitViewModel by viewModels { VisitViewModelFactory() }
    private lateinit var momentAdapter: MomentAdapter
    private var visitId by Delegates.notNull<Long>()
    private var memoryId by Delegates.notNull<Long>()
    private var memoryTitle by Delegates.notNull<String>()
    private val deleteDialog =
        DeleteDialogFragment {
            viewModel.deleteVisit(visitId)
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        visitId = arguments?.getLong(MOMENT_ID_KEY) ?: return
        memoryId = arguments?.getLong(MEMORY_ID_KEY) ?: return
        memoryTitle = arguments?.getString(MEMORY_TITLE_KEY) ?: return
        initAdapter()
        initToolbarHandler()
        observeData()
        viewModel.fetchVisitDetailData(visitId)
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
        viewModel.visitDefault.observe(viewLifecycleOwner) { visitDefault ->
            momentAdapter.updateMomentDefault(visitDefault)
        }
        viewModel.visitLogs.observe(viewLifecycleOwner) { visitLogs ->
            momentAdapter.updateVisitLogs(visitLogs)
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
        val visitUpdateLauncher = (activity as MainActivity).visitUpdateLauncher
        VisitUpdateActivity.startWithResultLauncher(
            visitId = visitId,
            memoryId = memoryId,
            memoryTitle = memoryTitle,
            context = requireContext(),
            activityLauncher = visitUpdateLauncher,
        )
    }

    companion object {
        const val MOMENT_ID_KEY = "momentId"
    }
}
