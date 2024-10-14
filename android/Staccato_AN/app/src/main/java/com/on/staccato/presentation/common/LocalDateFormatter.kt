package com.on.staccato.presentation.common

import android.view.View
import com.on.staccato.R
import java.time.LocalDate
import java.time.LocalDateTime

fun View.getFormattedLocalDate(setNowDate: LocalDate): String =
    setNowDate.let {
        val year = setNowDate.year
        val month = setNowDate.monthValue
        val day = setNowDate.dayOfMonth
        resources.getString(R.string.all_date_kr_format)
            .format(year, month, day)
    }

fun View.getFormattedLocalDateTime(setNowDateTime: LocalDateTime): String =
    setNowDateTime.let {
        val year = setNowDateTime.year
        val month = setNowDateTime.monthValue
        val day = setNowDateTime.dayOfMonth
        val hour = if (setNowDateTime.hour % 12 == 0) 12 else setNowDateTime.hour % 12
        val noonText =
            if (setNowDateTime.hour < 12) {
                resources.getString(R.string.all_am)
            } else {
                resources.getString(
                    R.string.all_pm,
                )
            }
        resources.getString(R.string.all_date_time_am_pm_kr_format)
            .format(year, month, day, noonText, hour)
    }
