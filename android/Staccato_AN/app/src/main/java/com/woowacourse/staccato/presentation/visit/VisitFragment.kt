package com.woowacourse.staccato.presentation.visit

import android.app.ProgressDialog.show
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.DeleteDialogFragment
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentVisitBinding
import com.woowacourse.staccato.presentation.ToolbarHandler
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.visit.adapter.VisitAdapter
import com.woowacourse.staccato.presentation.visit.viewmodel.VisitViewModel
import com.woowacourse.staccato.presentation.visit.viewmodel.VisitViewModelFactory
import com.woowacourse.staccato.presentation.visitupdate.VisitUpdateActivity

class VisitFragment :
    BindingFragment<FragmentVisitBinding>(R.layout.fragment_visit), ToolbarHandler {
    private val viewModel: VisitViewModel by viewModels { VisitViewModelFactory() }
    private lateinit var visitAdapter: VisitAdapter
    private val deleteDialog = DeleteDialogFragment { findNavController().popBackStack() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        initAdapter()
        initToolbarHandler()
        observeData()
        viewModel.fetchVisitDetailData()
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
            this.requireActivity(),
            visitUpdateLauncher,
        )
    }
}
