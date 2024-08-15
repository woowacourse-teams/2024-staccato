package com.woowacourse.staccato.presentation.memoryupdate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.woowacourse.staccato.R
import com.woowacourse.staccato.data.StaccatoClient.memoryApiService
import com.woowacourse.staccato.data.memory.TravelDefaultRepository
import com.woowacourse.staccato.data.memory.TravelRemoteDataSource
import com.woowacourse.staccato.databinding.ActivityTravelUpdateBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.memory.TravelFragment.Companion.TRAVEL_ID_KEY
import com.woowacourse.staccato.presentation.memoryupdate.viewmodel.TravelUpdateViewModel
import com.woowacourse.staccato.presentation.memoryupdate.viewmodel.TravelUpdateViewModelFactory
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visitcreation.OnUrisSelectedListener

class TravelUpdateActivity : BindingActivity<ActivityTravelUpdateBinding>(), TravelUpdateHandler, OnUrisSelectedListener {
    override val layoutResourceId = R.layout.activity_travel_update
    private val travelId by lazy { intent.getLongExtra(TRAVEL_ID_KEY, DEFAULT_TRAVEL_ID) }
    private val viewModel: TravelUpdateViewModel by viewModels {
        TravelUpdateViewModelFactory(
            travelId,
            TravelDefaultRepository(TravelRemoteDataSource(memoryApiService)),
        )
    }
    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val dateRangePicker = buildDateRangePicker()

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        navigateToTravel()
        fetchTravel()
        updateTravelPeriod()
        observeIsUpdateSuccess()
        showErrorToast()
    }

    override fun onPeriodSelectionClicked() {
        dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())
    }

    override fun onSaveClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        showToast(getString(R.string.travel_update_posting))
        viewModel.updateTravel(this)
    }

    override fun onPhotoAttachClicked() {
        photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.setImage(uris.first())
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
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            finish()
        }
    }

    private fun showErrorToast() {
        viewModel.errorMessage.observe(this) {
            window.clearFlags(FLAG_NOT_TOUCHABLE)
            showToast(it)
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
