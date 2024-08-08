package com.woowacourse.staccato.presentation.travelupdate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.woowacourse.staccato.EventLoggingManager
import com.woowacourse.staccato.EventLoggingManager.Companion.CONTENT_TYPE_BUTTON
import com.woowacourse.staccato.EventLoggingManager.Companion.NAME_TRAVEL_UPDATE
import com.woowacourse.staccato.R
import com.woowacourse.staccato.data.StaccatoClient.travelApiService
import com.woowacourse.staccato.data.travel.TravelDefaultRepository
import com.woowacourse.staccato.data.travel.TravelRemoteDataSource
import com.woowacourse.staccato.databinding.ActivityTravelUpdateBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.travel.TravelFragment.Companion.TRAVEL_ID_KEY
import com.woowacourse.staccato.presentation.travelupdate.viewmodel.TravelUpdateViewModel
import com.woowacourse.staccato.presentation.travelupdate.viewmodel.TravelUpdateViewModelFactory
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visitcreation.OnUrisSelectedListener

class TravelUpdateActivity : BindingActivity<ActivityTravelUpdateBinding>(), TravelUpdateHandler, OnUrisSelectedListener {
    override val layoutResourceId = R.layout.activity_travel_update
    private val travelId by lazy { intent.getLongExtra(TRAVEL_ID_KEY, DEFAULT_TRAVEL_ID) }
    private val viewModel: TravelUpdateViewModel by viewModels {
        TravelUpdateViewModelFactory(
            travelId,
            TravelDefaultRepository(TravelRemoteDataSource(travelApiService)),
        )
    }
    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val dateRangePicker = buildDateRangePicker()

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var eventLoggingManager: EventLoggingManager

    override fun initStartView(savedInstanceState: Bundle?) {
        setEventLoggingManager()
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
        showToast(getString(R.string.travel_update_posting))
        viewModel.updateTravel(this)
        eventLoggingManager.logEvent(
            binding.btnTravelUpdateSave.id.toString(),
            NAME_TRAVEL_UPDATE,
            CONTENT_TYPE_BUTTON,
        )
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

    private fun setEventLoggingManager() {
        firebaseAnalytics = Firebase.analytics
        eventLoggingManager = EventLoggingManager(firebaseAnalytics)
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

    private fun showErrorToast() {
        viewModel.errorMessage.observe(this) {
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
