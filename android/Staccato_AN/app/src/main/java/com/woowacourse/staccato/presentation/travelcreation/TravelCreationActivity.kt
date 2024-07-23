package com.woowacourse.staccato.presentation.travelcreation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityTravelCreationBinding
import com.woowacourse.staccato.presentation.base.BindingActivity

class TravelCreationActivity : BindingActivity<ActivityTravelCreationBinding>() {
    override val layoutResourceId = R.layout.activity_travel_creation

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.btnTravelCreateDone.setOnClickListener {
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
            Intent(context, TravelCreationActivity::class.java).apply {
                // putExtra(EXTRA_TRAVEL_ID, travelId)
                activityLauncher.launch(this)
            }
        }
    }
}
