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
import com.woowacourse.staccato.data.StaccatoClient.MemoryApiService
import com.woowacourse.staccato.data.memory.MemoryDefaultRepository
import com.woowacourse.staccato.data.memory.MemoryRemoteDataSource
import com.woowacourse.staccato.databinding.ActivityMemoryUpdateBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.memoryupdate.viewmodel.MemoryUpdateViewModel
import com.woowacourse.staccato.presentation.memoryupdate.viewmodel.MemoryUpdateViewModelFactory
import com.woowacourse.staccato.presentation.util.showToast
import com.woowacourse.staccato.presentation.visitcreation.OnUrisSelectedListener

class MemoryUpdateActivity : BindingActivity<ActivityMemoryUpdateBinding>(), MemoryUpdateHandler, OnUrisSelectedListener {
    override val layoutResourceId = R.layout.activity_memory_update
    private val memoryId by lazy { intent.getLongExtra(MEMORY_ID_KEY, DEFAULT_MEMORY_ID) }
    private val viewModel: MemoryUpdateViewModel by viewModels {
        MemoryUpdateViewModelFactory(
            memoryId,
            MemoryDefaultRepository(MemoryRemoteDataSource(MemoryApiService)),
        )
    }
    private val photoAttachFragment = PhotoAttachFragment()
    private val fragmentManager: FragmentManager = supportFragmentManager
    private val dateRangePicker = buildDateRangePicker()

    override fun initStartView(savedInstanceState: Bundle?) {
        initBinding()
        navigateToMemory()
        fetchMemory()
        updateMemoryPeriod()
        observeIsUpdateSuccess()
        showErrorToast()
    }

    override fun onPeriodSelectionClicked() {
        dateRangePicker.show(supportFragmentManager, dateRangePicker.toString())
    }

    override fun onSaveClicked() {
        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        showToast(getString(R.string.memory_update_posting))
        viewModel.updateMemory(this)
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

    private fun navigateToMemory() {
        binding.toolbarMemoryUpdate.setNavigationOnClickListener {
            finish()
        }
    }

    private fun fetchMemory() {
        viewModel.fetchMemory()
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
