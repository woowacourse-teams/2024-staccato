package com.on.staccato.presentation.memoryupdate

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
import com.on.staccato.databinding.ActivityMemoryUpdateBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.on.staccato.presentation.memoryupdate.viewmodel.MemoryUpdateViewModel
import com.on.staccato.presentation.staccatocreation.OnUrisSelectedListener
import com.on.staccato.presentation.util.getSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemoryUpdateActivity :
    BindingActivity<ActivityMemoryUpdateBinding>(),
    MemoryUpdateHandler,
    OnUrisSelectedListener {
    override val layoutResourceId = R.layout.activity_memory_update
    private val memoryId by lazy { intent.getLongExtra(MEMORY_ID_KEY, DEFAULT_MEMORY_ID) }
    private val viewModel: MemoryUpdateViewModel by viewModels()

    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val dateRangePicker = buildDateRangePicker()
    private var currentSnackBar: Snackbar? = null

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        navigateToMemory()
        fetchMemory()
        updateMemoryPeriod()
        observeIsUpdateSuccess()
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
        viewModel.updateMemory()
    }

    override fun onPhotoAttachClicked() {
        if (!photoAttachFragment.isAdded) {
            photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
        }
    }

    override fun onPhotoDeletionClicked() {
        currentSnackBar?.dismiss()
        viewModel.setThumbnailUri(null)
        viewModel.setThumbnailUrl(null)
    }

    override fun onUrisSelected(vararg uris: Uri) {
        currentSnackBar?.dismiss()
        viewModel.setThumbnailUri(uris.first())
        viewModel.createThumbnailUrl(this, uris.first())
    }

    private fun buildDateRangePicker() =
        MaterialDatePicker.Builder.dateRangePicker().setTheme(R.style.DatePickerStyle).setSelection(
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

    private fun navigateToMemory() {
        binding.toolbarMemoryUpdate.setNavigationOnClickListener {
            finish()
        }
    }

    private fun fetchMemory() {
        viewModel.fetchMemory(memoryId)
    }

    private fun updateMemoryPeriod() {
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate: Long = selection.first
            val endDate: Long = selection.second
            viewModel.setMemoryPeriod(startDate, endDate)
        }
    }

    private fun observeIsUpdateSuccess() {
        viewModel.isUpdateSuccess.observe(this) { isUpdateSuccess ->
            navigateToMemory(isUpdateSuccess)
        }
    }

    private fun navigateToMemory(isUpdateSuccess: Boolean) {
        if (isUpdateSuccess) {
            val intent = Intent().putExtra(MEMORY_ID_KEY, memoryId)
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

    private fun handleError() {
        viewModel.error.observe(this) { error ->
            when (error) {
                is MemoryUpdateError.MemoryInitialization -> handleInitializeFail(error)
                is MemoryUpdateError.Thumbnail -> handleCreatePhotoUrlFail(error)
                is MemoryUpdateError.MemoryUpdate -> handleMemoryUpdateFail(error)
            }
        }
    }

    private fun handleInitializeFail(error: MemoryUpdateError.MemoryInitialization) {
        finish()
        showToast(error.message)
    }

    private fun handleCreatePhotoUrlFail(error: MemoryUpdateError.Thumbnail) {
        showExceptionSnackBar(error.message) { reCreateThumbnailUrl(error.uri) }
    }

    private fun handleMemoryUpdateFail(error: MemoryUpdateError.MemoryUpdate) {
        window.clearFlags(FLAG_NOT_TOUCHABLE)
        showExceptionSnackBar(error.message) { reUpdateMemory() }
    }

    private fun reCreateThumbnailUrl(uri: Uri) {
        viewModel.createThumbnailUrl(this, uri)
    }

    private fun reUpdateMemory() {
        viewModel.updateMemory()
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
        private const val DEFAULT_MEMORY_ID = 0L

        fun startWithResultLauncher(
            memoryId: Long,
            context: Context,
            activityLauncher: ActivityResultLauncher<Intent>,
        ) {
            Intent(context, MemoryUpdateActivity::class.java).apply {
                putExtra(MEMORY_ID_KEY, memoryId)
                activityLauncher.launch(this)
            }
        }
    }
}
