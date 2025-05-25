package com.on.staccato.presentation.common.color

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.on.staccato.R
import com.on.staccato.theme.Blue
import com.on.staccato.theme.Blue15
import com.on.staccato.theme.Brown
import com.on.staccato.theme.Brown15
import com.on.staccato.theme.DarkBlue
import com.on.staccato.theme.DarkBrown
import com.on.staccato.theme.DarkGray
import com.on.staccato.theme.DarkGreen
import com.on.staccato.theme.DarkIndigo
import com.on.staccato.theme.DarkMint
import com.on.staccato.theme.DarkOrange
import com.on.staccato.theme.DarkPink
import com.on.staccato.theme.DarkPurple
import com.on.staccato.theme.DarkRed
import com.on.staccato.theme.DarkYellow
import com.on.staccato.theme.Gray
import com.on.staccato.theme.Gray15
import com.on.staccato.theme.Green
import com.on.staccato.theme.Green15
import com.on.staccato.theme.Indigo
import com.on.staccato.theme.Indigo15
import com.on.staccato.theme.LightBlue
import com.on.staccato.theme.LightBrown
import com.on.staccato.theme.LightGray
import com.on.staccato.theme.LightGreen
import com.on.staccato.theme.LightIndigo
import com.on.staccato.theme.LightMint
import com.on.staccato.theme.LightOrange
import com.on.staccato.theme.LightPink
import com.on.staccato.theme.LightPurple
import com.on.staccato.theme.LightRed
import com.on.staccato.theme.LightYellow
import com.on.staccato.theme.Mint
import com.on.staccato.theme.Mint15
import com.on.staccato.theme.Orange
import com.on.staccato.theme.Orange15
import com.on.staccato.theme.Pink
import com.on.staccato.theme.Pink15
import com.on.staccato.theme.Purple
import com.on.staccato.theme.Purple15
import com.on.staccato.theme.Red
import com.on.staccato.theme.Red15
import com.on.staccato.theme.White
import com.on.staccato.theme.Yellow
import com.on.staccato.theme.Yellow15

enum class CategoryColor(
    val label: String,
    @ColorRes val colorRes: Int,
    @DrawableRes val markerRes: Int,
    val color: Color,
    val iconBackgroundColor: Color,
    val textColor: Color,
    val index: Int,
) {
    LIGHT_RED(
        label = "light_red",
        colorRes = R.color.light_red,
        markerRes = R.drawable.icon_marker_4x_light_red,
        color = LightRed,
        iconBackgroundColor = Red15,
        textColor = DarkRed,
        index = 0,
    ),
    RED(
        label = "red",
        colorRes = R.color.red,
        markerRes = R.drawable.icon_marker_4x_red,
        color = Red,
        iconBackgroundColor = Red15,
        textColor = White,
        index = 1,
    ),
    LIGHT_ORANGE(
        label = "light_orange",
        colorRes = R.color.light_orange,
        markerRes = R.drawable.icon_marker_4x_light_orange,
        color = LightOrange,
        iconBackgroundColor = Orange15,
        textColor = White,
        index = 2,
    ),
    ORANGE(
        label = "orange",
        colorRes = R.color.orange,
        markerRes = R.drawable.icon_marker_4x_orange,
        color = Orange,
        iconBackgroundColor = Orange15,
        textColor = DarkOrange,
        index = 3,
    ),
    LIGHT_YELLOW(
        label = "light_yellow",
        colorRes = R.color.light_yellow,
        markerRes = R.drawable.icon_marker_4x_light_yellow,
        color = Yellow,
        iconBackgroundColor = Yellow15,
        textColor = DarkYellow,
        index = 4,
    ),
    YELLOW(
        label = "yellow",
        colorRes = R.color.yellow,
        markerRes = R.drawable.icon_marker_4x_yellow,
        color = LightYellow,
        iconBackgroundColor = Yellow15,
        textColor = DarkYellow,
        index = 5,
    ),
    LIGHT_GREEN(
        label = "light_green",
        colorRes = R.color.light_green,
        markerRes = R.drawable.icon_marker_4x_light_green,
        color = LightGreen,
        iconBackgroundColor = Green15,
        textColor = DarkGreen,
        index = 6,
    ),
    GREEN(
        label = "green",
        colorRes = R.color.green,
        markerRes = R.drawable.icon_marker_4x_green,
        color = Green,
        iconBackgroundColor = Green15,
        textColor = DarkGreen,
        index = 7,
    ),
    LIGHT_MINT(
        label = "light_mint",
        colorRes = R.color.light_mint,
        markerRes = R.drawable.icon_marker_4x_light_mint,
        color = LightMint,
        iconBackgroundColor = Mint15,
        textColor = DarkMint,
        index = 8,
    ),
    MINT(
        label = "mint",
        colorRes = R.color.mint,
        markerRes = R.drawable.icon_marker_4x_mint,
        color = Mint,
        iconBackgroundColor = Mint15,
        textColor = DarkMint,
        index = 9,
    ),
    LIGHT_BLUE(
        label = "light_blue",
        colorRes = R.color.light_blue,
        markerRes = R.drawable.icon_marker_4x_light_blue,
        color = LightBlue,
        iconBackgroundColor = Blue15,
        textColor = DarkBlue,
        index = 10,
    ),
    BLUE(
        label = "blue",
        colorRes = R.color.blue,
        markerRes = R.drawable.icon_marker_4x_blue,
        color = Blue,
        iconBackgroundColor = Blue15,
        textColor = White,
        index = 11,
    ),
    LIGHT_INDIGO(
        label = "light_indigo",
        colorRes = R.color.light_indigo,
        markerRes = R.drawable.icon_marker_4x_light_indigo,
        color = LightIndigo,
        iconBackgroundColor = Indigo15,
        textColor = DarkIndigo,
        index = 12,
    ),
    INDIGO(
        label = "indigo",
        colorRes = R.color.indigo,
        markerRes = R.drawable.icon_marker_4x_indigo,
        color = Indigo,
        iconBackgroundColor = Indigo15,
        textColor = White,
        index = 13,
    ),
    LIGHT_PURPLE(
        label = "light_purple",
        colorRes = R.color.light_purple,
        markerRes = R.drawable.icon_marker_4x_light_purple,
        color = LightPurple,
        iconBackgroundColor = Purple15,
        textColor = DarkPurple,
        index = 14,
    ),
    PURPLE(
        label = "purple",
        colorRes = R.color.purple,
        markerRes = R.drawable.icon_marker_4x_purple,
        color = Purple,
        iconBackgroundColor = Purple15,
        textColor = White,
        index = 15,
    ),
    LIGHT_PINK(
        label = "light_pink",
        colorRes = R.color.light_pink,
        markerRes = R.drawable.icon_marker_4x_light_pink,
        color = LightPink,
        iconBackgroundColor = Pink15,
        textColor = DarkPink,
        index = 16,
    ),
    PINK(
        label = "pink",
        colorRes = R.color.pink,
        markerRes = R.drawable.icon_marker_4x_pink,
        color = Pink,
        iconBackgroundColor = Pink15,
        textColor = White,
        index = 17,
    ),
    LIGHT_BROWN(
        label = "light_brown",
        colorRes = R.color.light_brown,
        markerRes = R.drawable.icon_marker_4x_light_brown,
        color = LightBrown,
        iconBackgroundColor = Brown15,
        textColor = DarkBrown,
        index = 18,
    ),
    BROWN(
        label = "brown",
        colorRes = R.color.brown,
        markerRes = R.drawable.icon_marker_4x_brown,
        color = Brown,
        iconBackgroundColor = Brown15,
        textColor = White,
        index = 19,
    ),
    LIGHT_GRAY(
        label = "light_gray",
        colorRes = R.color.light_gray,
        markerRes = R.drawable.icon_marker_4x_light_gray,
        color = LightGray,
        iconBackgroundColor = Gray15,
        textColor = DarkGray,
        index = 20,
    ),
    GRAY(
        label = "gray",
        colorRes = R.color.gray,
        markerRes = R.drawable.icon_marker_4x_gray,
        color = Gray,
        iconBackgroundColor = Gray15,
        textColor = White,
        index = 21,
    ),
    ;

    companion object {
        fun getAllColors() = entries.toList()

        fun getCategoryColorBy(label: String) = entries.firstOrNull { it.label == label } ?: GRAY

        fun getMarkerResBy(label: String) = entries.firstOrNull { it.label == label }?.markerRes ?: GRAY.markerRes

        fun getColorResBy(label: String) = entries.firstOrNull { it.label == label }?.colorRes ?: GRAY.colorRes

        fun getColorBy(label: String) = entries.firstOrNull { it.label == label }?.color ?: GRAY.color

        fun getIconBackgroundColorBy(label: String) =
            entries.firstOrNull { it.label == label }?.iconBackgroundColor ?: GRAY.iconBackgroundColor

        fun getTextColorBy(label: String) = entries.firstOrNull { it.label == label }?.textColor ?: GRAY.textColor
    }
}
