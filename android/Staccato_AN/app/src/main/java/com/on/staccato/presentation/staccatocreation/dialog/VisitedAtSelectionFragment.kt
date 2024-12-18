package com.on.staccato.presentation.staccatocreation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.staccato.databinding.FragmentVisitedAtSelectionBinding
import com.on.staccato.domain.model.MemoryCandidate.Companion.buildNumberPickerDates
import java.time.LocalDate
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

    private var selectedYear by Delegates.notNull<Int>()
    private var selectedMonth by Delegates.notNull<Int>()
    private var selectedDay by Delegates.notNull<Int>()
    private var selectedHour by Delegates.notNull<Int>()

    private lateinit var handler: VisitedAtSelectionHandler

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
        setYearsByPeriod()
        binding.years = years
        setupPickers()
        setupPickerListeners()
        initConfirmButton()
        initPickerPosition()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnVisitedAtSelected(newHandler: VisitedAtSelectionHandler) {
        handler = newHandler
    }

    fun setVisitedAtPeriod(
        startAt: LocalDate?,
        endAt: LocalDate?,
    ) {
        yearCandidates = buildNumberPickerDates(startAt, endAt)
    }

    fun updateSelectedVisitedAt(visitedAt: LocalDateTime) {
        selectedYear = visitedAt.year
        selectedMonth = visitedAt.monthValue
        selectedDay = visitedAt.dayOfMonth
        selectedHour = visitedAt.hour
    }

    private fun setYearsByPeriod() {
        years = yearCandidates.keys.toList()
        setMonthsBy(selectedYear)
    }

    private fun setMonthsBy(year: Int) {
        monthCandidates = yearCandidates[year] ?: throw IllegalArgumentException()
        months = monthCandidates.keys.toList()
        setDaysBy(selectedMonth)
    }

    private fun setDaysBy(month: Int) {
        days = monthCandidates[month] ?: throw IllegalArgumentException()
    }

    private fun initPickerPosition() {
        binding.pickerYear.setPickerValue(yearCandidates.keys.toList(), selectedYear)
        binding.pickerMonth.setPickerValue(monthCandidates.keys.toList(), selectedMonth)
        binding.pickerDay.setPickerValue(days, selectedDay)
        binding.pickerHours.setPickerValue(hours, selectedHour)
    }

    private fun NumberPicker.setPickerValue(
        targetList: List<Int>,
        targetKey: Int,
    ) {
        this.value = targetList.indexOf(targetKey).takeIf { it >= 0 } ?: 0
    }

    private fun resetMonthsBy(year: Int) {
        monthCandidates = yearCandidates[year] ?: throw IllegalArgumentException()
        months = monthCandidates.keys.toList()
        selectedMonth = months.first()
        resetDaysBy(selectedMonth)
    }

    private fun resetDaysBy(month: Int) {
        days = monthCandidates[month] ?: throw IllegalArgumentException()
        selectedDay = days.first()
    }

    private fun setupPickers() {
        binding.pickerYear.updatePickerValues(years, selectedYear)
        binding.pickerMonth.updatePickerValues(months, selectedMonth)
        binding.pickerDay.updatePickerValues(days, selectedDay)
        binding.pickerHours.updatePickerValues(hours, selectedHour)
    }

    private fun setupPickerListeners() {
        setYearPickerListener()
        setMonthPickerListener()
        setDayPickerListener()
        setHourPickerListener()
    }

    private fun setYearPickerListener() {
        binding.pickerYear.setOnValueChangedListener { _, _, newItemPosition ->
            selectedYear = years[newItemPosition]
            resetMonthsBy(selectedYear)
            binding.pickerMonth.updatePickerValues(months, selectedMonth)
            binding.pickerDay.updatePickerValues(days, selectedDay)
        }
    }

    private fun setMonthPickerListener() {
        binding.pickerMonth.setOnValueChangedListener { _, _, newItemPosition ->
            selectedMonth = months[newItemPosition]
            resetDaysBy(selectedMonth)
            binding.pickerDay.updatePickerValues(days, selectedDay)
        }
    }

    private fun setDayPickerListener() {
        binding.pickerDay.setOnValueChangedListener { _, _, newItemPosition ->
            selectedDay = days[newItemPosition]
        }
    }

    private fun setHourPickerListener() {
        binding.pickerHours.setOnValueChangedListener { _, _, newItemPosition ->
            selectedHour = hours[newItemPosition]
        }
    }

    private fun initConfirmButton() {
        binding.btnVisitedAtConfirm.setOnClickListener {
            handler.onConfirmClicked(
                LocalDateTime.of(selectedYear, selectedMonth, selectedDay, selectedHour, 0, 0),
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
