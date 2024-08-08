package com.woowacourse.staccato.presentation.visit

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
import com.woowacourse.staccato.presentation.travel.TravelFragment.Companion.TRAVEL_ID_KEY
import com.woowacourse.staccato.presentation.travel.TravelFragment.Companion.TRAVEL_TITLE_KEY
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visit.adapter.VisitAdapter
import com.woowacourse.staccato.presentation.visit.viewmodel.VisitViewModel
import com.woowacourse.staccato.presentation.visit.viewmodel.VisitViewModelFactory
import com.woowacourse.staccato.presentation.visitupdate.VisitUpdateActivity
import kotlin.properties.Delegates

class VisitFragment :
    BindingFragment<FragmentVisitBinding>(R.layout.fragment_visit), ToolbarHandler {
    private val viewModel: VisitViewModel by viewModels { VisitViewModelFactory() }
    private lateinit var visitAdapter: VisitAdapter
    private var visitId by Delegates.notNull<Long>()
    private var travelId by Delegates.notNull<Long>()
    private var travelTitle by Delegates.notNull<String>()
    private val deleteDialog =
        DeleteDialogFragment {
            viewModel.deleteVisit(visitId)
        }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        visitId = arguments?.getLong(VISIT_ID_KEY) ?: return
        travelId = arguments?.getLong(TRAVEL_ID_KEY) ?: return
        travelTitle = arguments?.getString(TRAVEL_TITLE_KEY) ?: return
        initAdapter()
        initToolbarHandler()
        observeData()
        viewModel.fetchVisitDetailData(visitId)
    }

    private fun initAdapter() {
        visitAdapter = VisitAdapter(mutableListOf())
        binding.rvVisitDetail.adapter = visitAdapter
    }

    private fun initToolbarHandler() {
        binding.toolbarHandler = this
        binding.includeVisitToolbar.toolbarDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeData() {
        viewModel.visitDefault.observe(viewLifecycleOwner) { visitDefault ->
            visitAdapter.updateVisitDefault(visitDefault)
        }
        viewModel.visitLogs.observe(viewLifecycleOwner) { visitLogs ->
            visitAdapter.updateVisitLogs(visitLogs)
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
            travelId = travelId,
            travelTitle = travelTitle,
            context = requireContext(),
            activityLauncher = visitUpdateLauncher,
        )
    }

    companion object {
        const val VISIT_ID_KEY = "visitId"
    }
}
