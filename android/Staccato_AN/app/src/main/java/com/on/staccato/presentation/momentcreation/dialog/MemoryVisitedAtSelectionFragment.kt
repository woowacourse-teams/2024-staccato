package com.on.staccato.presentation.momentcreation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
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

    private val hours = (0 until 24).toList()

    private lateinit var memoryCandidates: Map<MemoryCandidate, Map<Int, Map<Int, List<Int>>>>
    private lateinit var yearCandidates: Map<Int, Map<Int, List<Int>>>
    private lateinit var monthCandidates: Map<Int, List<Int>>

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
        initPickerPosition()
    }

    // 1. numberPicker position 설정 (key 값의 index or 0)
    private fun initPickerPosition() {
        binding.pickerMemory.setPickerValue(memoryCandidates.keys.toList(), keyMemory)
        binding.pickerYear.setPickerValue(yearCandidates.keys.toList(), keyYear)
        binding.pickerMonth.setPickerValue(monthCandidates.keys.toList(), keyMonth)
        binding.pickerDay.setPickerValue(days, keyDay)
        binding.pickerHours.setPickerValue(hours, keyHour)
    }

    private fun <T : Any> NumberPicker.setPickerValue(
        targetList: List<T>,
        targetKey: T,
    ) {
        this.value = targetList.indexOf(targetKey).takeIf { it >= 0 } ?: 0
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
        selectedMemory: MemoryCandidate,
        selectedVisitedAt: LocalDateTime,
    ) {
        memoryCandidates =
            newMemories.associateWith {
                buildNumberPickerDates(it.startAt, it.endAt)
            }
        memories = newMemories
        initKeyWithSelectedValues(selectedVisitedAt, selectedMemory)
        setYearsBy(keyMemory)
    }

    // 2. key 설정 (선택된 추억 / 날짜가 있으면 해당 정보, 없으면 첫번째 값)
    private fun initKeyWithSelectedValues(
        selectedVisitedAt: LocalDateTime,
        selectedMemory: MemoryCandidate,
    ) {
        keyMemory = selectedMemory
        keyYear = selectedVisitedAt.year
        keyMonth = selectedVisitedAt.monthValue
        keyDay = selectedVisitedAt.dayOfMonth
        keyHour = selectedVisitedAt.hour
    }

    // 선택된 추억에 대한 값들 초기화
    private fun setYearsBy(memory: MemoryCandidate) {
        yearCandidates = memoryCandidates[memory] ?: throw IllegalArgumentException()
        years = yearCandidates.keys.toList()
        setMonthsBy(keyYear)
    }

    private fun setMonthsBy(year: Int) {
        monthCandidates = yearCandidates[year] ?: throw IllegalArgumentException()
        months = monthCandidates.keys.toList()
        setDaysBy(keyMonth)
    }

    private fun setDaysBy(month: Int) {
        days = monthCandidates[month] ?: throw IllegalArgumentException()
    }

    // 0번째로 리셋
    private fun resetYearsBy(memory: MemoryCandidate) {
        yearCandidates = memoryCandidates[memory] ?: throw IllegalArgumentException()
        years = yearCandidates.keys.toList()
        keyYear = years.first()
        resetMonthsBy(keyYear)
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
        binding.pickerMemory.updatePickerValues(memories.map { it.memoryTitle }, keyMemory)
        binding.pickerYear.updatePickerValues(years, keyYear)
        binding.pickerMonth.updatePickerValues(months, keyMonth)
        binding.pickerDay.updatePickerValues(days, keyDay)
        binding.pickerHours.updatePickerValues(hours, keyHour)
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
            binding.pickerYear.updatePickerValues(years, keyYear)
            binding.pickerMonth.updatePickerValues(months, keyMonth)
            binding.pickerDay.updatePickerValues(days, keyDay)
        }
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
        binding.btnMemoryVisitedAtConfirm.setOnClickListener {
            handler.onConfirmClicked(
                memoryUiModel = keyMemory,
                visitedAt = LocalDateTime.of(keyYear, keyMonth, keyDay, keyHour, 0, 0),
            )
            dismiss()
        }
    }

    private fun <T : Any> NumberPicker.updatePickerValues(
        list: List<T>,
        key: T,
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
