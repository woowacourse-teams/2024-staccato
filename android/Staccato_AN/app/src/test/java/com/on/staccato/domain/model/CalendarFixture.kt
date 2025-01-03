package com.on.staccato.domain.model

import java.time.LocalDate

val december10thTo20thCalendar =
    DateCalendar.of(
        2024,
        12,
        LocalDate.of(2024, 12, 10),
        LocalDate.of(2024, 12, 20),
    )

val december1stTo15thMonthCalendar =
    MonthCalendar.of(
        year = 2024,
        periodStart = LocalDate.of(2024, 6, 1),
        periodEnd = LocalDate.of(2024, 12, 15),
    )

val aprilToAugustCalendar =
    MonthCalendar.of(
        2024,
        LocalDate.of(2024, 4, 1),
        LocalDate.of(2024, 8, 1),
    )

val juneDateCalendar =
    DateCalendar.of(
        2024,
        6,
    )
