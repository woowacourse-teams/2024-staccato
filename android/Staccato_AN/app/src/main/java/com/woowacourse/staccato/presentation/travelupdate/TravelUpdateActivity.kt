package com.woowacourse.staccato.presentation.travelupdate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityTravelUpdateBinding
import com.woowacourse.staccato.presentation.base.BindingActivity

class TravelUpdateActivity : BindingActivity<ActivityTravelUpdateBinding>() {
    override val layoutResourceId = R.layout.activity_travel_update

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.btnTravelUpdateDone.setOnClickListener {
            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, TravelUpdateActivity::class.java).apply {
                // putExtra(EXTRA_TRAVEL_ID, travelId)
                activityLauncher.launch(this)
            }
        }
    }
}
