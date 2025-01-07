package com.on.staccato.presentation.staccatocreation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.staccato.databinding.FragmentVisitedAtSelectionBinding
import com.on.staccato.domain.model.YearCalendar
import com.on.staccato.domain.model.YearCalendar.Companion.hours
import java.time.LocalDate
import java.time.LocalDateTime

class VisitedAtSelectionFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentVisitedAtSelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var yearCalendar: YearCalendar

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
        binding.years = yearCalendar.getAvailableYears()
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

    fun initCalendarByPeriod(
        startAt: LocalDate? = null,
        endAt: LocalDate? = null,
    ) {
        yearCalendar = YearCalendar.of(startAt, endAt)
    }

    fun updateSelectedVisitedAt(visitedAt: LocalDateTime) {
        yearCalendar.initSelectedDateTime(visitedAt)
    }

    private fun initPickerPosition() {
        binding.pickerYear.setPickerValue(yearCalendar.getAvailableYears(), yearCalendar.selectedYear)
        binding.pickerMonth.setPickerValue(yearCalendar.getAvailableMonths(), yearCalendar.selectedMonth)
        binding.pickerDay.setPickerValue(yearCalendar.getAvailableDates(), yearCalendar.selectedDate)
        binding.pickerHours.setPickerValue(hours, yearCalendar.selectedHour)
    }

    private fun NumberPicker.setPickerValue(
        targetList: List<Int>,
        targetKey: Int,
    ) {
        this.value = targetList.indexOf(targetKey).takeIf { it >= 0 } ?: 0
    }

    private fun setupPickers() {
        binding.pickerYear.updatePickerValues(yearCalendar.getAvailableYears(), yearCalendar.selectedYear)
        binding.pickerMonth.updatePickerValues(yearCalendar.getAvailableMonths(), yearCalendar.selectedMonth)
        binding.pickerDay.updatePickerValues(yearCalendar.getAvailableDates(), yearCalendar.selectedDate)
        binding.pickerHours.updatePickerValues(hours, yearCalendar.selectedHour)
    }

    private fun setupPickerListeners() {
        setYearPickerListener()
        setMonthPickerListener()
        setDayPickerListener()
        setHourPickerListener()
    }

    private fun setYearPickerListener() {
        binding.pickerYear.setOnValueChangedListener { _, _, newItemPosition ->
            yearCalendar.changeSelectedYear(yearCalendar.getAvailableYears()[newItemPosition])
            binding.pickerMonth.updatePickerValues(yearCalendar.getAvailableMonths(), yearCalendar.selectedMonth)
            binding.pickerDay.updatePickerValues(yearCalendar.getAvailableDates(), yearCalendar.selectedDate)
        }
    }

    private fun setMonthPickerListener() {
        binding.pickerMonth.setOnValueChangedListener { _, _, newItemPosition ->
            yearCalendar.changeSelectedMonth(yearCalendar.getAvailableMonths()[newItemPosition])
            binding.pickerDay.updatePickerValues(yearCalendar.getAvailableDates(), yearCalendar.selectedDate)
        }
    }

    private fun setDayPickerListener() {
        binding.pickerDay.setOnValueChangedListener { _, _, newItemPosition ->
            yearCalendar.changeSelectedDate(yearCalendar.getAvailableDates()[newItemPosition])
        }
    }

    private fun setHourPickerListener() {
        binding.pickerHours.setOnValueChangedListener { _, _, newItemPosition ->
            yearCalendar.changeSelectedHour(hours[newItemPosition])
        }
    }

    private fun initConfirmButton() {
        binding.btnVisitedAtConfirm.setOnClickListener {
            handler.onConfirmClicked(
                LocalDateTime.of(
                    yearCalendar.selectedYear,
                    yearCalendar.selectedMonth,
                    yearCalendar.selectedDate,
                    yearCalendar.selectedHour,
                    0,
                    0,
                ),
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
