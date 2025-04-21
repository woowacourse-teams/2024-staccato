package com.on.staccato.presentation.common.color

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.on.staccato.R

enum class CategoryColor(
    val label: String,
    @ColorRes val colorRes: Int,
    @DrawableRes val markerRes: Int,
) {
    LIGHT_RED(
        "light_red",
        R.color.light_red,
        R.drawable.icon_marker_2x_light_red,
    ),
    RED(
        "red",
        R.color.red,
        R.drawable.icon_marker_2x_red,
    ),
    LIGHT_ORANGE(
        "light_orange",
        R.color.light_orange,
        R.drawable.icon_marker_2x_light_orange,
    ),
    ORANGE(
        "orange",
        R.color.orange,
        R.drawable.icon_marker_2x_orange,
    ),
    LIGHT_YELLOW(
        "light_yellow",
        R.color.light_yellow,
        R.drawable.icon_marker_2x_light_yellow,
    ),
    YELLOW(
        "yellow",
        R.color.yellow,
        R.drawable.icon_marker_2x_yellow,
    ),
    LIGHT_GREEN(
        "light_green",
        R.color.light_green,
        R.drawable.icon_marker_2x_light_green,
    ),
    GREEN(
        "green",
        R.color.green,
        R.drawable.icon_marker_2x_green,
    ),
    LIGHT_MINT(
        "light_mint",
        R.color.light_mint,
        R.drawable.icon_marker_2x_light_mint,
    ),
    MINT(
        "mint",
        R.color.mint,
        R.drawable.icon_marker_2x_mint,
    ),
    LIGHT_BLUE(
        "light_blue",
        R.color.light_blue,
        R.drawable.icon_marker_2x_light_blue,
    ),
    BLUE(
        "blue",
        R.color.blue,
        R.drawable.icon_marker_2x_blue,
    ),
    LIGHT_INDIGO(
        "light_indigo",
        R.color.light_indigo,
        R.drawable.icon_marker_2x_light_indigo,
    ),
    INDIGO(
        "indigo",
        R.color.indigo,
        R.drawable.icon_marker_2x_indigo,
    ),
    LIGHT_PURPLE(
        "light_purple",
        R.color.light_purple,
        R.drawable.icon_marker_2x_light_purple,
    ),
    PURPLE(
        "purple",
        R.color.purple,
        R.drawable.icon_marker_2x_purple,
    ),
    LIGHT_PINK(
        "light_pink",
        R.color.light_pink,
        R.drawable.icon_marker_2x_light_pink,
    ),
    PINK(
        "pink",
        R.color.pink,
        R.drawable.icon_marker_2x_pink,
    ),
    LIGHT_BROWN(
        "light_brown",
        R.color.light_brown,
        R.drawable.icon_marker_2x_light_brown,
    ),
    BROWN(
        "brown",
        R.color.brown,
        R.drawable.icon_marker_2x_brown,
    ),
    LIGHT_GRAY(
        "light_gray",
        R.color.light_gray,
        R.drawable.icon_marker_2x_light_gray,
    ),
    GRAY(
        "gray",
        R.color.gray,
        R.drawable.icon_marker_2x_gray,
    ),
    ;

    companion object {
        fun getAllColors() = entries.toList()

        fun getColorBy(label: String) = entries.firstOrNull { it.label == label } ?: GRAY

        fun getMarkerResBy(label: String) = entries.firstOrNull { it.label == label }?.markerRes ?: GRAY.markerRes

        fun getColorResBy(label: String) = entries.firstOrNull { it.label == label }?.colorRes ?: GRAY.colorRes
    }
}
