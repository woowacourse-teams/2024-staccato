package com.woowacourse.staccato.presentation.visit

import android.os.Bundle
import android.view.View
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentVisitBinding
import com.woowacourse.staccato.presentation.base.BindingFragment
import com.woowacourse.staccato.presentation.main.MainActivity
import com.woowacourse.staccato.presentation.visitupdate.VisitUpdateActivity

class VisitFragment :
    BindingFragment<FragmentVisitBinding>(R.layout.fragment_visit) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.btnVisitUpdate.setOnClickListener {
            val visitUpdateLauncher = (activity as MainActivity).visitUpdateLauncher
            VisitUpdateActivity.startWithResultLauncher(
                this.requireActivity(),
                visitUpdateLauncher,
            )
//            findNavController().navigate(R.id.action_travelFragment_to_travelUpdateFragment)
        }
    }
}
