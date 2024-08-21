package com.woowacourse.staccato.presentation.moment.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentMomentDetailBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.moment.viewmodel.MomentViewModel
import com.woowacourse.staccato.presentation.moment.viewmodel.MomentViewModelFactory

class MomentDetailFragment :
    BindingFragment<FragmentMomentDetailBinding>(R.layout.fragment_moment_detail) {
    private lateinit var photoAdapter: HorizontalPhotoAdapter
    private val momentDetailViewModel: MomentDetailViewModel by viewModels()
    private val momentViewModel: MomentViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    ) {
        MomentViewModelFactory()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        initAdapter()
        observeViewModel()
    }

    private fun initAdapter() {
        photoAdapter = HorizontalPhotoAdapter()
        binding.rvPhotoHorizontal.adapter = photoAdapter
    }

    private fun observeViewModel() {
        momentViewModel.momentDetail.observe(viewLifecycleOwner) { momentDetail ->
            Log.d("hodu: MomentDetailFragment", "momentDetail of MomentViewModel: $momentDetail")
            momentDetailViewModel.setMomentDetail(momentDetail)
        }

        momentDetailViewModel.momentDetail.observe(viewLifecycleOwner) { momentDetail ->
            Log.d("hodu: MomentDetailFragment", "momentDetail of MomentDetailViewModel: $momentDetail")
            photoAdapter.submitList(momentDetail.momentImageUrls)
        }
    }
}
