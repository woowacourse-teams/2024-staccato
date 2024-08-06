package com.woowacourse.staccato.presentation.travelupdate

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
import com.woowacourse.staccato.databinding.ActivityTravelUpdateBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.travel.TravelFragment.Companion.TRAVEL_ID_KEY
import com.woowacourse.staccato.presentation.travelupdate.viewmodel.TravelUpdateViewModel
import com.woowacourse.staccato.presentation.travelupdate.viewmodel.TravelUpdateViewModelFactory

class TravelUpdateActivity : BindingActivity<ActivityTravelUpdateBinding>(), TravelUpdateHandler {
    override val layoutResourceId = R.layout.activity_travel_update
    private val travelId by lazy { intent.getLongExtra(TRAVEL_ID_KEY, DEFAULT_TRAVEL_ID) }
    private val viewModel: TravelUpdateViewModel by viewModels {
        TravelUpdateViewModelFactory(
            travelId,
            TravelDefaultRepository(TravelRemoteDataSource(travelApiService)),
        )
    }
    private val dateRangePicker = buildDateRangePicker()

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        navigateToTravel()
        fetchTravel()
        updateTravelPeriod()
        observeIsUpdateSuccess()
    }

    override fun onPeriodSelectionClicked() {
        dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())
    }

    override fun onSaveClicked() {
        viewModel.updateTravel()
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

    private fun navigateToTravel() {
        binding.toolbarTravelUpdate.setNavigationOnClickListener {
            finish()
        }
    }

    private fun fetchTravel() {
        viewModel.fetchTravel()
    }

    private fun updateTravelPeriod() {
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate: Long = selection.first
            val endDate: Long = selection.second
            viewModel.setTravelPeriod(startDate, endDate)
        }
    }

    private fun observeIsUpdateSuccess() {
        viewModel.isUpdateSuccess.observe(this) { isUpdateSuccess ->
            navigateToTravel(isUpdateSuccess)
        }
    }

    private fun navigateToTravel(isUpdateSuccess: Boolean) {
        if (isUpdateSuccess) {
            val intent = Intent().putExtra(TRAVEL_ID_KEY, travelId)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    companion object {
        private const val DEFAULT_TRAVEL_ID = 0L

        fun startWithResultLauncher(
            travelId: Long,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, TravelUpdateActivity::class.java).apply {
                putExtra(TRAVEL_ID_KEY, travelId)
                activityLauncher.launch(this)
            }
        }
    }
}
