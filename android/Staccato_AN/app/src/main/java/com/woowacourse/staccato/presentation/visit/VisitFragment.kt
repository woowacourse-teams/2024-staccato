package com.woowacourse.staccato.presentation.visit

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.FragmentVisitBinding
import com.woowacourse.staccato.presentation.base.BindingFragment

class VisitFragment :
    BindingFragment<FragmentVisitBinding>(R.layout.fragment_visit) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.btnVisit.setOnClickListener {
            findNavController().navigate(R.id.action_visitFragment_to_visitCreationFragment)
        }
    }
}
