package com.on.staccato.presentation.momentcreation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.staccato.databinding.FragmentVisitedAtSelectionBinding
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.domain.model.MemoryCandidate.Companion.buildNumberPickerDates
import java.time.LocalDateTime
import kotlin.properties.Delegates

class VisitedAtSelectionFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentVisitedAtSelectionBinding? = null
    private val binding get() = _binding!!

    private val hours = (0 until 24).toList()

    private lateinit var yearCandidates: Map<Int, Map<Int, List<Int>>>
    private lateinit var monthCandidates: Map<Int, List<Int>>

    private var years = listOf<Int>()
    private var months = listOf<Int>()
    private var days = listOf<Int>()

    private var keyYear by Delegates.notNull<Int>()
    private var keyMonth by Delegates.notNull<Int>()
    private var keyDay by Delegates.notNull<Int>()
    private var keyHour by Delegates.notNull<Int>()

    private lateinit var handler: VisitedAtSelectionHandler

    fun setOnVisitedAtSelected(newHandler: VisitedAtSelectionHandler) {
        handler = newHandler
    }

    fun initDateCandidates(
        selectedMemory: MemoryCandidate,
        selectedVisitedAt: LocalDateTime,
    ) {
        yearCandidates = buildNumberPickerDates(selectedMemory.startAt, selectedMemory.endAt)
        initKeyWithSelectedValues(selectedVisitedAt)
        years = yearCandidates.keys.toList()
        setMonthsBy(keyYear)
    }

    fun initKeyWithSelectedValues(selectedVisitedAt: LocalDateTime) {
        keyYear = selectedVisitedAt.year
        keyMonth = selectedVisitedAt.monthValue
        keyDay = selectedVisitedAt.dayOfMonth
        keyHour = selectedVisitedAt.hour
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentVisitedAtSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.years = years
        setupPickers()
        setupPickerListeners()
        initConfirmButton()
        initPickerPosition()
    }

    private fun initPickerPosition() {
        binding.pickerYear.setPickerValue(yearCandidates.keys.toList(), keyYear)
        binding.pickerMonth.setPickerValue(monthCandidates.keys.toList(), keyMonth)
        binding.pickerDay.setPickerValue(days, keyDay)
        binding.pickerHours.setPickerValue(hours, keyHour)
    }

    private fun NumberPicker.setPickerValue(
        targetList: List<Int>,
        targetKey: Int,
    ) {
        this.value = targetList.indexOf(targetKey).takeIf { it >= 0 } ?: 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setMonthsBy(year: Int) {
        monthCandidates = yearCandidates[year] ?: throw IllegalArgumentException()
        months = monthCandidates.keys.toList()
        setDaysBy(keyMonth)
    }

    private fun setDaysBy(month: Int) {
        days = monthCandidates[month] ?: throw IllegalArgumentException()
    }

    private fun resetMonthsBy(year: Int) {
        monthCandidates = yearCandidates[year] ?: throw IllegalArgumentException()
        months = monthCandidates.keys.toList()
        keyMonth = months.first()
        resetDaysBy(keyMonth)
    }

    private fun resetDaysBy(month: Int) {
        days = monthCandidates[month] ?: throw IllegalArgumentException()
        keyDay = days.first()
    }

    private fun setupPickers() {
        binding.pickerYear.updatePickerValues(years, keyYear)
        binding.pickerMonth.updatePickerValues(months, keyMonth)
        binding.pickerDay.updatePickerValues(days, keyDay)
        binding.pickerHours.updatePickerValues(hours, keyHour)
    }

    private fun setupPickerListeners() {
        setYearPickerListener()
        setMonthPickerListener()
        setDayPickerListener()
        setHourPickerListener()
    }

    private fun setYearPickerListener() {
        binding.pickerYear.setOnValueChangedListener { _, _, newItemPosition ->
            keyYear = years[newItemPosition]
            resetMonthsBy(keyYear)
            binding.pickerMonth.updatePickerValues(months, keyMonth)
            binding.pickerDay.updatePickerValues(days, keyDay)
        }
    }

    private fun setMonthPickerListener() {
        binding.pickerMonth.setOnValueChangedListener { _, _, newItemPosition ->
            keyMonth = months[newItemPosition]
            resetDaysBy(keyMonth)
            binding.pickerDay.updatePickerValues(days, keyDay)
        }
    }

    private fun setDayPickerListener() {
        binding.pickerDay.setOnValueChangedListener { _, _, newItemPosition ->
            keyDay = days[newItemPosition]
        }
    }

    private fun setHourPickerListener() {
        binding.pickerHours.setOnValueChangedListener { _, _, newItemPosition ->
            keyHour = hours[newItemPosition]
        }
    }

    private fun initConfirmButton() {
        binding.btnVisitedAtConfirm.setOnClickListener {
            handler.onConfirmClicked(
                LocalDateTime.of(keyYear, keyMonth, keyDay, keyHour, 0, 0),
            )
            dismiss()
        }
    }

    private fun NumberPicker.updatePickerValues(
        list: List<Int>,
        key: Int,
    ) {
        apply {
            displayedValues = null
            setPickerValue(list, key)
            minValue = 0
            maxValue = (list.size - 1).coerceAtLeast(0)
            wrapSelectorWheel = false
            displayedValues = list.map { it.toString() }.toTypedArray()
        }
    }

    companion object {
        const val TAG = "MemorySelectionModalBottomSheet"
    }
}
