package com.woowacourse.staccato.presentation.travelupdate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.woowacourse.staccato.R
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.travelcreation.TravelCreationHandler
import com.woowacourse.staccato.presentation.travelupdate.viewmodel.TravelUpdateViewModel
import com.woowacourse.staccato.presentation.travelupdate.viewmodel.TravelUpdateViewModelFactory

class TravelUpdateActivity : BindingActivity<com.woowacourse.staccato.databinding.ActivityTravelUpdateBinding>(), TravelCreationHandler {
    override val layoutResourceId = R.layout.activity_travel_update
    private val viewModel: TravelUpdateViewModel by viewModels { TravelUpdateViewModelFactory() }

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        navigateToTravel()
    }

    override fun onPeriodSelectionClicked() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTheme(R.style.DatePickerStyle)
                .setSelection(
                    androidx.core.util.Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds(),
                    ),
                ).build()

        dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate: Long = selection.first
            val endDate: Long = selection.second
            viewModel.setTravelPeriod(startDate, endDate)
        }
    }

    override fun onSaveClicked() {
        val resultIntent = Intent()
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.handler = this
    }

    private fun navigateToTravel() {
        binding.toolbarTravelUpdate.setNavigationOnClickListener {
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
