package com.woowacourse.staccato.presentation.memorycreation

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
import com.woowacourse.staccato.data.StaccatoClient.imageApiService
import com.woowacourse.staccato.data.StaccatoClient.memoryApiService
import com.woowacourse.staccato.data.image.ImageDefaultRepository
import com.woowacourse.staccato.data.memory.MemoryDefaultRepository
import com.woowacourse.staccato.data.memory.MemoryRemoteDataSource
import com.woowacourse.staccato.databinding.ActivityMemoryCreationBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.memorycreation.viewmodel.MemoryCreationViewModel
import com.woowacourse.staccato.presentation.memorycreation.viewmodel.MemoryCreationViewModelFactory
import com.woowacourse.staccato.presentation.momentcreation.OnUrisSelectedListener
import com.woowacourse.staccato.presentation.util.showToast

class MemoryCreationActivity : BindingActivity<ActivityMemoryCreationBinding>(), MemoryCreationHandler, OnUrisSelectedListener {
    override val layoutResourceId = R.layout.activity_memory_creation
    private val viewModel: MemoryCreationViewModel by viewModels {
        MemoryCreationViewModelFactory(
            MemoryDefaultRepository(MemoryRemoteDataSource(memoryApiService)),
            ImageDefaultRepository(imageApiService),
        )
    }
    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val dateRangePicker = buildDateRangePicker()

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        navigateToMap()
        updateMemoryPeriod()
        observeCreatedMemoryId()
        showErrorToast()
    }

    override fun onPeriodSelectionClicked() {
        dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())
    }

    override fun onSaveClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        showToast(getString(R.string.memory_creation_posting))
        viewModel.createMemory()
    }

    override fun onPhotoAttachClicked() {
        if (!photoAttachFragment.isAdded) {
            photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
        }
    }

    override fun onImageDeletionClicked() {
        viewModel.setThumbnailUrl(null)
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.createThumbnailUrl(this, uris.first())
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
        binding.toolbarMemoryCreation.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateMemoryPeriod() {
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate: Long = selection.first
            val endDate: Long = selection.second
            viewModel.setMemoryPeriod(startDate, endDate)
        }
    }

    private fun observeCreatedMemoryId() {
        viewModel.createdMemoryId.observe(this) { memoryId ->
            val resultIntent = Intent()
            resultIntent.putExtra(MEMORY_ID_KEY, memoryId)
            setResult(RESULT_OK, resultIntent)
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
        fun startWithResultLauncher(
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, MemoryCreationActivity::class.java).apply {
                activityLauncher.launch(this)
            }
        }
    }
}
