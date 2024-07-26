package com.woowacourse.staccato.presentation.travelcreation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import com.woowacourse.staccato.R
import com.woowacourse.staccato.data.StaccatoClient.travelApiService
import com.woowacourse.staccato.data.travel.TravelDefaultRepository
import com.woowacourse.staccato.data.travel.TravelRemoteDataSource
import com.woowacourse.staccato.databinding.ActivityTravelCreationBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.timeline.TimelineFragment.Companion.TRAVEL_ID_KEY
import com.woowacourse.staccato.presentation.travelcreation.viewmodel.TravelCreationViewModel
import com.woowacourse.staccato.presentation.travelcreation.viewmodel.TravelCreationViewModelFactory

class TravelCreationActivity : BindingActivity<ActivityTravelCreationBinding>(), TravelCreationHandler {
    override val layoutResourceId = R.layout.activity_travel_creation
    private val viewModel: TravelCreationViewModel by viewModels {
        TravelCreationViewModelFactory(TravelDefaultRepository(TravelRemoteDataSource(travelApiService)))
    }
    private val dateRangePicker = buildDateRangePicker()

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        navigateToMap()
        updateTravelPeriod()
        observeCreatedTravelId()
    }

    override fun onPeriodSelectionClicked() {
        dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())
    }

    // TODO: viewModel 이 핸들러 가지도록 수정
    override fun onSaveClicked() {
        viewModel.createTravel()
    }

    private fun buildDateRangePicker() =
        MaterialDatePicker.Builder.dateRangePicker()
            .setTheme(R.style.DatePickerStyle)
            .setSelection(
                Pair(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds(),
                ),
            ).build()

    private fun initBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.handler = this
    }

    private fun navigateToMap() {
        binding.toolbarTravelCreation.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateTravelPeriod() {
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate: Long = selection.first
            val endDate: Long = selection.second
            viewModel.setTravelPeriod(startDate, endDate)
        }
    }

    private fun observeCreatedTravelId() {
        viewModel.createdTravelId.observe(this) { travelId ->
            val resultIntent = Intent()
            resultIntent.putExtra(TRAVEL_ID_KEY, travelId)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, TravelCreationActivity::class.java).apply {
                activityLauncher.launch(this)
            }
        }
    }
}
