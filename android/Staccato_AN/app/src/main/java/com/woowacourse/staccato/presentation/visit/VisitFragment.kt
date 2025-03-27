package com.woowacourse.staccato.presentation.visit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.DeleteDialogFragment
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentVisitBinding
import com.woowacourse.staccato.presentation.ToolbarHandler
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.travel.TravelFragment
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
    private val deleteDialog = DeleteDialogFragment { findNavController().popBackStack() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        visitId = arguments?.getLong(VISIT_ID_KEY) ?: return
        travelId = arguments?.getLong(TravelFragment.TRAVEL_ID_KEY) ?: return
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
                Toast.makeText(this.requireActivity(), "방문을 불러올 수 없어요!", Toast.LENGTH_SHORT)
                    .show()
                findNavController().popBackStack()
            }
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
            travelId,
            visitId,
            this.requireActivity(),
            visitUpdateLauncher,
        )
    }

    companion object {
        const val VISIT_ID_KEY = "visitId"
    }
}
