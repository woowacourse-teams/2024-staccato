package com.woowacourse.staccato.presentation.visitcreation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import com.woowacourse.staccato.R
import com.woowacourse.staccato.databinding.ActivityVisitCreationBinding
import com.woowacourse.staccato.presentation.base.BindingActivity

class VisitCreationActivity : BindingActivity<ActivityVisitCreationBinding>() {
    override val layoutResourceId = R.layout.activity_visit_creation

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.btnVisitCreateDone.setOnClickListener {
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
            Intent(context, VisitCreationActivity::class.java).apply {
                // putExtra(EXTRA_VISIT_ID, visitId)
                activityLauncher.launch(this)
            }
        }
    }
}
