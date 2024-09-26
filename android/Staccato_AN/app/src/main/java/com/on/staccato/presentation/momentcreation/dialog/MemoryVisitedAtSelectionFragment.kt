package com.on.staccato.presentation.momentcreation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.on.staccato.databinding.FragmentMemoryVisitedAtSelectionBinding
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.domain.model.MemoryCandidate.Companion.buildNumberPickerDates
import com.on.staccato.domain.model.MemoryCandidates
import java.time.LocalDateTime
import kotlin.properties.Delegates

class MemoryVisitedAtSelectionFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentMemoryVisitedAtSelectionBinding? = null
    private val binding get() = _binding!!

    private val hours = List(24) { it }

    private lateinit var memoryCandidates: Map<MemoryCandidate, Map<Int, Map<Int, List<Int>>>>
    private lateinit var yearCandidates: Map<Int, Map<Int, List<Int>>>
    private lateinit var monthCandidates: Map<Int, List<Int>>
    private lateinit var dayCandidates: List<Int>

    private lateinit var memories: List<MemoryCandidate>
    private var years = listOf<Int>()
    private var months = listOf<Int>()
    private var days = listOf<Int>()

    private lateinit var keyMemory: MemoryCandidate
    private var keyYear by Delegates.notNull<Int>()
    private var keyMonth by Delegates.notNull<Int>()
    private var keyDay by Delegates.notNull<Int>()
    private var keyHour by Delegates.notNull<Int>()

    private lateinit var handler: MemoryVisitedAtSelectionHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMemoryVisitedAtSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        binding.memoryCandidates = MemoryCandidates(memories)
        setupPickers()
        setupPickerListeners()
        initConfirmButton()
        initPickerPositionWithKey()
    }

    // 1. numberPicker position 설정 (key 값의 index or 0)
    private fun initPickerPositionWithKey() {
        binding.pickerMemory.value = findKeyIndex(memoryCandidates.keys.toList(), keyMemory)
        binding.pickerYear.value = findKeyIndex(yearCandidates.keys.toList(), keyYear)
        binding.pickerMonth.value = findKeyIndex(monthCandidates.keys.toList(), keyMonth)
        binding.pickerDay.value = findKeyIndex(dayCandidates, keyDay)
        binding.pickerHours.value = findKeyIndex(hours, keyHour)
    }

    private fun <T : Any> findKeyIndex(
        targetList: List<T>,
        targetKey: T,
    ): Int {
        return targetList.indexOf(targetKey).takeIf { it >= 0 } ?: 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setOnSelected(newHandler: MemoryVisitedAtSelectionHandler) {
        handler = newHandler
    }

    fun initMemoryCandidates(
        newMemories: List<MemoryCandidate>,
        selectedMemory: MemoryCandidate?,
        selectedVisitedAt: LocalDateTime?,
    ) {
        if (newMemories.isNotEmpty()) {
            keyHour = hours[0]
            memoryCandidates =
                newMemories.associateWith {
                    buildNumberPickerDates(it.startAt, it.endAt)
                }
            memories = newMemories
            keyMemory = newMemories[0]
            resetYearsBy(keyMemory)
        }
        initKeyWithSelectedValues(selectedVisitedAt, selectedMemory)
    }

    // 2. key 설정 (선택된 추억 / 날짜가 있으면 해당 정보, 없으면 첫번째 값)
    private fun initKeyWithSelectedValues(
        selectedVisitedAt: LocalDateTime?,
        selectedMemory: MemoryCandidate?,
    ) {
        if (selectedVisitedAt != null && selectedMemory != null) {
            keyMemory = selectedMemory
            keyYear = selectedVisitedAt.year
            keyMonth = selectedVisitedAt.monthValue
            keyDay = selectedVisitedAt.dayOfMonth
            keyHour = selectedVisitedAt.hour
        }
    }

    // 선택된 추억에 대한 값들 초기화
    private fun resetYearsBy(keyMemory: MemoryCandidate) {
        yearCandidates = memoryCandidates[keyMemory] ?: throw IllegalArgumentException()
        years = yearCandidates.map { it.key }
        keyYear = years[0] // 추가
        resetMonthsBy(keyYear)
    }

    private fun resetMonthsBy(keyYear: Int) {
        monthCandidates = yearCandidates[keyYear] ?: throw IllegalArgumentException()
        months = monthCandidates.map { it.key }
        keyMonth = months[0] // 추가
        resetDaysBy(keyMonth)
    }

    private fun resetDaysBy(keyMonth: Int) {
        dayCandidates = monthCandidates[keyMonth] ?: throw IllegalArgumentException()
        days = dayCandidates
        keyDay = days[0]
    }

    private fun setupPickers() {
        updateMemoryPickerValues()
        updateYearPickerValues()
        updateMonthPickerValues()
        updateDayPickerValues()
        updateHourPickerValues()
    }

    private fun setupPickerListeners() {
        setMemoryPickerListener()
        setYearPickerListener()
        setMonthPickerListener()
        setDayPickerListener()
        setHourPickerListener()
    }

    private fun setMemoryPickerListener() {
        binding.pickerMemory.setOnValueChangedListener { _, _, newItemPosition ->
            keyMemory = memories[newItemPosition]
            resetYearsBy(keyMemory)
            updateYearPickerValues()
            updateMonthPickerValues()
            updateDayPickerValues()
        }
    }

    private fun setYearPickerListener() {
        binding.pickerYear.apply {
            setOnValueChangedListener { _, _, newItemPosition ->
                keyYear = years[newItemPosition]
                resetMonthsBy(keyYear)
                updateMonthPickerValues()
                updateDayPickerValues()
            }
        }
    }

    private fun setMonthPickerListener() {
        binding.pickerMonth.apply {
            setOnValueChangedListener { _, _, newItemPosition ->
                keyMonth = months[newItemPosition]
                resetDaysBy(keyMonth)
                updateDayPickerValues()
            }
        }
    }

    private fun setDayPickerListener() {
        binding.pickerDay.apply {
            setOnValueChangedListener { _, _, newItemPosition ->
                keyDay = days[newItemPosition]
            }
        }
    }

    private fun setHourPickerListener() {
        binding.pickerHours.apply {
            setOnValueChangedListener { _, _, newItemPosition ->
                keyHour = hours[newItemPosition]
            }
        }
    }

    private fun updateMemoryPickerValues() {
        binding.pickerMemory.apply {
            displayedValues = null
            minValue = 0
            maxValue = (memories.size - 1).coerceAtLeast(0)
            wrapSelectorWheel = true
            displayedValues = memories.map { it.memoryTitle }.toTypedArray()
        }
    }

    private fun updateYearPickerValues() {
        binding.pickerYear.apply {
            displayedValues = null
            value = findKeyIndex(years, keyYear)
            minValue = 0
            maxValue = (years.size - 1).coerceAtLeast(0)
            wrapSelectorWheel = false
            displayedValues = years.map { it.toString() }.toTypedArray()
        }
    }

    private fun updateMonthPickerValues() {
        binding.pickerMonth.apply {
            value = findKeyIndex(months, keyMonth)
            displayedValues = null
            minValue = 0
            maxValue = (months.size - 1).coerceAtLeast(0)
            wrapSelectorWheel = false
            displayedValues = months.map { it.toString() }.toTypedArray()
        }
    }

    private fun updateDayPickerValues() {
        binding.pickerDay.apply {
            value = findKeyIndex(days, keyDay)
            displayedValues = null
            minValue = 0
            maxValue = (days.size - 1).coerceAtLeast(0)
            wrapSelectorWheel = false
            displayedValues = days.map { it.toString() }.toTypedArray()
        }
    }

    private fun updateHourPickerValues() {
        binding.pickerHours.apply {
            value = findKeyIndex(hours, keyHour)
            displayedValues = null
            minValue = 0
            maxValue = (hours.size - 1).coerceAtLeast(0)
            wrapSelectorWheel = false
            displayedValues = hours.map { it.toString() }.toTypedArray()
        }
    }

    private fun initConfirmButton() {
        binding.btnMemoryVisitedAtConfirm.setOnClickListener {
            handler.onConfirmClicked(
                memoryUiModel = keyMemory,
                visitedAt = LocalDateTime.of(keyYear, keyMonth, keyDay, keyHour, 0, 0),
            )
            dismiss()
        }
    }

    companion object {
        const val TAG = "MemorySelectionModalBottomSheet"
    }
}
