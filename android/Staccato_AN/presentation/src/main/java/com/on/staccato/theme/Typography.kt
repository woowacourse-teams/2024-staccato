package com.on.staccato.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.on.staccato.presentation.R

val Title1 =
    defaultTextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 20.sp,
    )

val Title2 =
    defaultTextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 20.sp,
    )

val Title3 =
    defaultTextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
    )

val Body1 =
    defaultTextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.Normal,
    )

val Body2 =
    defaultTextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
    )

val Body3 =
    defaultTextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp,
    )

val Body4 =
    defaultTextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
    )

val Body5 =
    defaultTextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
    )

val Body6 =
    defaultTextStyle(
        fontSize = 9.sp,
        fontWeight = FontWeight.SemiBold,
    )

// TODO: 디자인 시스템 정리 전 임시 Typography
val Regular11 =
    defaultTextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
    )

val SemiBold16 =
    defaultTextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
    )

private val Pretendard =
    FontFamily(
        Font(R.font.pretendard_regular, weight = FontWeight.Normal),
        Font(R.font.pretendard_medium, weight = FontWeight.Medium),
        Font(R.font.pretendard_semibold, weight = FontWeight.SemiBold),
        Font(R.font.pretendard_bold, weight = FontWeight.Bold),
    )

private fun defaultTextStyle(
    fontSize: TextUnit,
    fontWeight: FontWeight,
    lineHeight: TextUnit = TextUnit.Unspecified,
): TextStyle =
    TextStyle(
        fontSize = fontSize,
        fontWeight = fontWeight,
        lineHeight = lineHeight,
        fontFamily = Pretendard,
        letterSpacing = (-0.02).sp,
    )
