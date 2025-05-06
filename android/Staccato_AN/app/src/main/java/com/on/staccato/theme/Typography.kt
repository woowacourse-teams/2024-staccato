package com.on.staccato.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.on.staccato.R

val pretendard = FontFamily(
    Font(R.font.pretendard_regular),
    Font(R.font.pretendard_medium),
    Font(R.font.pretendard_semibold),
    Font(R.font.pretendard_bold),
)

val title1 =
    defaultTextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 20.sp,
    )

val title2 =
    defaultTextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 20.sp,
    )

val title3 =
    defaultTextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
    )

val body1 =
    defaultTextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal,
    )

val body2 =
    defaultTextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
    )

val body3 =
    defaultTextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp,
    )

val body4 =
    defaultTextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
    )

val body5 =
    defaultTextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
    )

fun defaultTextStyle(
    fontSize: TextUnit,
    fontWeight: FontWeight,
    lineHeight: TextUnit = TextUnit.Unspecified,
) = TextStyle(
    fontSize = fontSize,
    fontWeight = fontWeight,
    lineHeight = lineHeight,
    fontFamily = pretendard,
    letterSpacing = (-0.02).sp,
)
