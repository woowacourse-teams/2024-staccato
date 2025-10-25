package com.on.staccato.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

fun buildStaccatoCountMarker() =
    ImageVector.Builder(
        name = "icon_staccato_count_marker",
        defaultWidth = 8.dp,
        defaultHeight = 10.dp,
        viewportWidth = 8f,
        viewportHeight = 10f,
    ).apply {
        path(
            fill = SolidColor(Color(0xFF949494)),
        ) {
            moveTo(7.99035f, 4.21898f)
            curveTo(7.99035f, 2.05963f, 6.20024f, 0.306641f, 3.99517f, 0.306641f)
            curveTo(1.78528f, 0.306641f, 0f, 2.05963f, 0f, 4.21898f)
            curveTo(0f, 4.95608f, 0.212304f, 5.64594f, 0.579011f, 6.23657f)
            curveTo(1.46683f, 7.76748f, 3.41616f, 9.4685f, 3.41616f, 9.4685f)
            curveTo(3.57539f, 9.6197f, 3.7877f, 9.69057f, 4f, 9.6953f)
            curveTo(4.2123f, 9.6953f, 4.41978f, 9.6197f, 4.58384f, 9.4685f)
            curveTo(4.58384f, 9.4685f, 6.52835f, 7.77221f, 7.42099f, 6.24602f)
            curveTo(7.78287f, 5.65539f, 8f, 4.96553f, 8f, 4.2237f)
            lineTo(7.99035f, 4.21898f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFFFFFFFF)),
        ) {
            moveTo(3.99509f, 6.09956f)
            curveTo(4.98801f, 6.09956f, 5.79292f, 5.29465f, 5.79292f, 4.30173f)
            curveTo(5.79292f, 3.30882f, 4.98801f, 2.50391f, 3.99509f, 2.50391f)
            curveTo(3.00218f, 2.50391f, 2.19727f, 3.30882f, 2.19727f, 4.30173f)
            curveTo(2.19727f, 5.29465f, 3.00218f, 6.09956f, 3.99509f, 6.09956f)
            close()
        }
    }.build()
