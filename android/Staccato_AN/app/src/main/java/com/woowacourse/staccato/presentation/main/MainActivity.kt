package com.woowacourse.staccato.presentation.main

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityMainBinding
import com.woowacourse.staccato.presentation.base.BindingActivity

class MainActivity : BindingActivity<ActivityMainBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_main

    override fun initStartView(savedInstanceState: Bundle?) {
        val behavior = BottomSheetBehavior.from(binding.clBottomSheet)
    }
}
