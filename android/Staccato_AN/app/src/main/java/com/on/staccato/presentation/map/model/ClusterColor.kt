package com.on.staccato.presentation.map.model

import androidx.annotation.ColorRes
import com.on.staccato.R

enum class ClusterColor(
    @ColorRes val outerCircle: Int,
    @ColorRes val innerCircle: Int,
) {
    MINT(
        outerCircle = R.color.mint_30,
        innerCircle = R.color.mint,
    ),
    BLUE(
        outerCircle = R.color.blue_30,
        innerCircle = R.color.blue,
    ),
    INDIGO(
        outerCircle = R.color.staccato_blue_30,
        innerCircle = R.color.staccato_blue,
    ),
}
