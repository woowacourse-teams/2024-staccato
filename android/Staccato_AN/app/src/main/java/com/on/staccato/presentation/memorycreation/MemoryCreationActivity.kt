package com.on.staccato.presentation.memorycreation

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
import com.on.staccato.R
import com.on.staccato.databinding.ActivityMemoryCreationBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.memorycreation.viewmodel.MemoryCreationViewModel
import com.on.staccato.presentation.momentcreation.OnUrisSelectedListener
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemoryCreationActivity :
    BindingActivity<ActivityMemoryCreationBinding>(),
    MemoryCreationHandler,
    OnUrisSelectedListener {
    override val layoutResourceId = R.layout.activity_memory_creation
    private val viewModel: MemoryCreationViewModel by viewModels()

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
        if (!dateRangePicker.isAdded) {
            dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())
        }
    }

    override fun onSaveClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        viewModel.createMemory()
    }

    override fun onPhotoAttachClicked() {
        if (!photoAttachFragment.isAdded) {
            photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
        }
    }

    override fun onImageDeletionClicked() {
        viewModel.setThumbnailUri(null)
        viewModel.setThumbnailUrl(null)
    }

    override fun onUrisSelected(vararg uris: Uri) {
        viewModel.createThumbnailUrl(this, uris.first())
        viewModel.setThumbnailUri(uris.first())
        showToast(getString(R.string.all_posting_photo))
    }

    private fun buildDateRangePicker() =
        MaterialDatePicker.Builder.dateRangePicker()
            .setTheme(R.style.DatePickerStyle)
            .setSelection(
                Pair(
                    MaterialDatePicker.todayInUtcMilliseconds(),
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
