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
import com.on.staccato.databinding.ActivityCategoryCreationBinding
import com.on.staccato.presentation.base.BindingActivity
import com.on.staccato.presentation.common.PhotoAttachFragment
import com.on.staccato.presentation.category.CategoryFragment.Companion.CATEGORY_ID_KEY
import com.on.staccato.presentation.memorycreation.viewmodel.CategoryCreationViewModel
import com.on.staccato.presentation.staccatocreation.OnUrisSelectedListener
import com.on.staccato.presentation.util.getSnackBarWithAction
import com.on.staccato.presentation.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryCreationActivity :
    BindingActivity<ActivityCategoryCreationBinding>(),
    CategoryCreationHandler,
    OnUrisSelectedListener {
    override val layoutResourceId = R.layout.activity_category_creation
    private val viewModel: CategoryCreationViewModel by viewModels()

    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val dateRangePicker = buildDateRangePicker()
    private var currentSnackBar: Snackbar? = null

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        navigateToMap()
        updateCategoryPeriod()
        observeCreatedCategoryId()
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
        viewModel.createCategory()
    }

    override fun onPhotoAttachClicked() {
        if (!photoAttachFragment.isAdded) {
            photoAttachFragment.show(fragmentManager, PhotoAttachFragment.TAG)
        }
    }

    override fun onImageDeletionClicked() {
        currentSnackBar?.dismiss()
        viewModel.clearThumbnail()
    }

    override fun onUrisSelected(vararg uris: Uri) {
        currentSnackBar?.dismiss()
        viewModel.createThumbnailUrl(this, uris.first())
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
        binding.toolbarCategoryCreation.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateCategoryPeriod() {
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate: Long = selection.first
            val endDate: Long = selection.second
            viewModel.setCategoryPeriod(startDate, endDate)
        }
    }

    private fun observeCreatedCategoryId() {
        viewModel.createdCategoryId.observe(this) { categoryId ->
            val resultIntent = Intent()
            resultIntent.putExtra(CATEGORY_ID_KEY, categoryId)
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
                is CategoryCreationError.Thumbnail -> handleCreatePhotoUrlFail(error)
                is CategoryCreationError.CategoryCreation -> handleCreateException(error)
            }
        }
    }

    private fun handleCreatePhotoUrlFail(error: CategoryCreationError.Thumbnail) {
        showExceptionSnackBar(error.message) { reCreateThumbnailUrl(error.uri) }
    }

    private fun handleCreateException(error: CategoryCreationError.CategoryCreation) {
        window.clearFlags(FLAG_NOT_TOUCHABLE)
        showExceptionSnackBar(error.message) { recreateCategory() }
    }

    private fun reCreateThumbnailUrl(uri: Uri) {
        viewModel.createThumbnailUrl(this, uri)
    }

    private fun recreateCategory() {
        viewModel.createCategory()
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
            Intent(context, CategoryCreationActivity::class.java).apply {
                activityLauncher.launch(this)
            }
        }
    }
}
