package com.on.staccato.presentation.common.color

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.on.staccato.R

enum class CategoryColor(
    val label: String,
    @ColorRes val colorRes: Int,
    @DrawableRes val markerRes: Int,
    val index: Int,
) {
    LIGHT_RED(
        "light_red",
        R.color.light_red,
        R.drawable.icon_marker_4x_light_red,
        0,
    ),
    RED(
        "red",
        R.color.red,
        R.drawable.icon_marker_4x_red,
        1,
    ),
    LIGHT_ORANGE(
        "light_orange",
        R.color.light_orange,
        R.drawable.icon_marker_4x_light_orange,
        2,
    ),
    ORANGE(
        "orange",
        R.color.orange,
        R.drawable.icon_marker_4x_orange,
        3,
    ),
    LIGHT_YELLOW(
        "light_yellow",
        R.color.light_yellow,
        R.drawable.icon_marker_4x_light_yellow,
        4,
    ),
    YELLOW(
        "yellow",
        R.color.yellow,
        R.drawable.icon_marker_4x_yellow,
        5,
    ),
    LIGHT_GREEN(
        "light_green",
        R.color.light_green,
        R.drawable.icon_marker_4x_light_green,
        6,
    ),
    GREEN(
        "green",
        R.color.green,
        R.drawable.icon_marker_4x_green,
        7,
    ),
    LIGHT_MINT(
        "light_mint",
        R.color.light_mint,
        R.drawable.icon_marker_4x_light_mint,
        8,
    ),
    MINT(
        "mint",
        R.color.mint,
        R.drawable.icon_marker_4x_mint,
        9,
    ),
    LIGHT_BLUE(
        "light_blue",
        R.color.light_blue,
        R.drawable.icon_marker_4x_light_blue,
        10,
    ),
    BLUE(
        "blue",
        R.color.blue,
        R.drawable.icon_marker_4x_blue,
        11,
    ),
    LIGHT_INDIGO(
        "light_indigo",
        R.color.light_indigo,
        R.drawable.icon_marker_4x_light_indigo,
        12,
    ),
    INDIGO(
        "indigo",
        R.color.indigo,
        R.drawable.icon_marker_4x_indigo,
        13,
    ),
    LIGHT_PURPLE(
        "light_purple",
        R.color.light_purple,
        R.drawable.icon_marker_4x_light_purple,
        14,
    ),
    PURPLE(
        "purple",
        R.color.purple,
        R.drawable.icon_marker_4x_purple,
        15,
    ),
    LIGHT_PINK(
        "light_pink",
        R.color.light_pink,
        R.drawable.icon_marker_4x_light_pink,
        16,
    ),
    PINK(
        "pink",
        R.color.pink,
        R.drawable.icon_marker_4x_pink,
        17,
    ),
    LIGHT_BROWN(
        "light_brown",
        R.color.light_brown,
        R.drawable.icon_marker_4x_light_brown,
        18,
    ),
    BROWN(
        "brown",
        R.color.brown,
        R.drawable.icon_marker_4x_brown,
        19,
    ),
    LIGHT_GRAY(
        "light_gray",
        R.color.light_gray,
        R.drawable.icon_marker_4x_light_gray,
        20,
    ),
    GRAY(
        "gray",
        R.color.gray,
        R.drawable.icon_marker_4x_gray,
        21,
    ),
    ;

    companion object {
        fun getAllColors() = entries.toList()

        fun getColorBy(label: String) = entries.firstOrNull { it.label == label } ?: GRAY

        fun getMarkerResBy(label: String) = entries.firstOrNull { it.label == label }?.markerRes ?: GRAY.markerRes

        fun getColorResBy(label: String) = entries.firstOrNull { it.label == label }?.colorRes ?: GRAY.colorRes
    }
}
