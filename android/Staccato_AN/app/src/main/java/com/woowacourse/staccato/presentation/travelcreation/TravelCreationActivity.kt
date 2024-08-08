package com.woowacourse.staccato.presentation.travelcreation

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
import com.woowacourse.staccato.EventLoggingManager.Companion.NAME_TRAVEL_CREATION
import com.woowacourse.staccato.R
import com.woowacourse.staccato.data.StaccatoClient.travelApiService
import com.woowacourse.staccato.data.travel.TravelDefaultRepository
import com.woowacourse.staccato.data.travel.TravelRemoteDataSource
import com.woowacourse.staccato.databinding.ActivityTravelCreationBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.timeline.TimelineFragment.Companion.TRAVEL_ID_KEY
import com.woowacourse.staccato.presentation.travelcreation.viewmodel.TravelCreationViewModel
import com.woowacourse.staccato.presentation.travelcreation.viewmodel.TravelCreationViewModelFactory
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visitcreation.OnUrisSelectedListener

class TravelCreationActivity : BindingActivity<ActivityTravelCreationBinding>(), TravelCreationHandler, OnUrisSelectedListener {
    override val layoutResourceId = R.layout.activity_travel_creation
    private val viewModel: TravelCreationViewModel by viewModels {
        TravelCreationViewModelFactory(TravelDefaultRepository(TravelRemoteDataSource(travelApiService)))
    }
    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val dateRangePicker = buildDateRangePicker()

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var eventLoggingManager: EventLoggingManager

    override fun initStartView(savedInstanceState: Bundle?) {
        setEventLoggingManager()
        initBinding()
        navigateToMap()
        updateTravelPeriod()
        observeCreatedTravelId()
        showErrorToast()
    }

    private fun setEventLoggingManager() {
        firebaseAnalytics = Firebase.analytics
        eventLoggingManager = EventLoggingManager(firebaseAnalytics)
    }

    override fun onPeriodSelectionClicked() {
        dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())
    }

    override fun onSaveClicked() {
        showToast(getString(R.string.travel_creation_posting))
        viewModel.createTravel(this)
        eventLoggingManager.logEvent(
            binding.btnTravelCreationSave.id.toString(),
            NAME_TRAVEL_CREATION,
            CONTENT_TYPE_BUTTON,
        )
    }

    override fun onPhotoAttachClicked() {
        photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.setImageUri(uris.first())
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

    private fun showErrorToast() {
        viewModel.errorMessage.observe(this) {
            showToast(it)
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
