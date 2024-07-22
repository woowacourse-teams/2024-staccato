package com.woowacourse.staccato.presentation.visitupdate

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentVisitCreationBinding
import com.woowacourse.staccato.presentation.base.BindingFragment

class VisitUpdateFragment :
    BindingFragment<FragmentVisitCreationBinding>(R.layout.fragment_visit_creation) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.tvTmpTitle.text = "방문 수정 화면"
        binding.btnVisitUpdateDone.setOnClickListener {
            findNavController().popBackStack(R.id.visitFragment, false)
        }
    }
}
