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
import com.google.android.material.snackbar.Snackbar
import com.on.staccato.R
import com.on.staccato.databinding.ActivityMemoryCreationBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.memorycreation.viewmodel.MemoryCreationViewModel
import com.on.staccato.presentation.staccatocreation.OnUrisSelectedListener
import com.on.staccato.presentation.util.getSnackBarWithAction
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
    private var currentSnackBar: Snackbar? = null

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        navigateToMap()
        updateMemoryPeriod()
        observeCreatedMemoryId()
        showErrorToast()
        handleError()
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
        currentSnackBar?.dismiss()
        viewModel.setThumbnailUri(null)
        viewModel.setThumbnailUrl(null)
    }

    override fun onUrisSelected(vararg uris: Uri) {
        currentSnackBar?.dismiss()
        viewModel.createThumbnailUrl(this, uris.first())
        viewModel.setThumbnailUri(uris.first())
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

    private fun handleError() {
        viewModel.error.observe(this) { error ->
            when (error) {
                is MemoryCreationError.Thumbnail -> handleCreatePhotoUrlFail(error)
                is MemoryCreationError.MemoryCreation -> handleCreateException(error)
            }
        }
    }

    private fun handleCreatePhotoUrlFail(error: MemoryCreationError.Thumbnail) {
        showExceptionSnackBar(error.message) { reCreateThumbnailUrl(error.uri) }
    }

    private fun handleCreateException(error: MemoryCreationError.MemoryCreation) {
        window.clearFlags(FLAG_NOT_TOUCHABLE)
        showExceptionSnackBar(error.message) { reCreateMemory() }
    }

    private fun reCreateThumbnailUrl(uri: Uri) {
        viewModel.createThumbnailUrl(this, uri)
    }

    private fun reCreateMemory() {
        viewModel.createMemory()
    }

    private fun showExceptionSnackBar(
        message: String,
        onRetryAction: () -> Unit,
    ) {
        currentSnackBar =
            binding.root.getSnackBarWithAction(
                message = message,
                actionLabel = R.string.all_retry,
                onAction = onRetryAction,
                length = Snackbar.LENGTH_INDEFINITE,
            ).apply { show() }
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
