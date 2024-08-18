package com.woowacourse.staccato.presentation.memoryupdate

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.woowacourse.staccato.R
import com.woowacourse.staccato.data.StaccatoClient.memoryApiService
import com.woowacourse.staccato.data.memory.MemoryDefaultRepository
import com.woowacourse.staccato.data.memory.MemoryRemoteDataSource
import com.woowacourse.staccato.databinding.ActivityMemoryUpdateBinding
import com.woowacourse.staccato.presentation.base.BindingActivity
import com.woowacourse.staccato.presentation.common.PhotoAttachFragment
import com.woowacourse.staccato.presentation.memory.MemoryFragment.Companion.MEMORY_ID_KEY
import com.woowacourse.staccato.presentation.memoryupdate.viewmodel.MemoryUpdateViewModel
import com.woowacourse.staccato.presentation.memoryupdate.viewmodel.MemoryUpdateViewModelFactory
import com.woowacourse.staccato.presentation.momentcreation.OnUrisSelectedListener
import com.woowacourse.staccato.presentation.util.showToast

class MemoryUpdateActivity :
    BindingActivity<ActivityMemoryUpdateBinding>(),
    MemoryUpdateHandler,
    OnUrisSelectedListener {
    override val layoutResourceId = R.layout.activity_memory_update
    private val memoryId by lazy { intent.getLongExtra(MEMORY_ID_KEY, DEFAULT_MEMORY_ID) }
    private val viewModel: MemoryUpdateViewModel by viewModels {
        MemoryUpdateViewModelFactory(
            memoryId,
            MemoryDefaultRepository(MemoryRemoteDataSource(memoryApiService)),
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

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (!isTouchInsideView(event, view)) {
                    clearFocusAndHideKeyboard(view)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun isTouchInsideView(
        event: MotionEvent,
        view: View,
    ): Boolean {
        val rect = android.graphics.Rect()
        view.getGlobalVisibleRect(rect)
        return rect.contains(event.rawX.toInt(), event.rawY.toInt())
    }

    private fun clearFocusAndHideKeyboard(view: View) {
        view.clearFocus()
        hideKeyboard(view)
    }

    private fun hideKeyboard(view: View) {
        val inputManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS,
        )
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
