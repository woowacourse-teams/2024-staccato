package com.on.staccato.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

fun buildFolder() =
    ImageVector.Builder(
        name = "icon_folder",
        defaultWidth = 17.dp,
        defaultHeight = 17.dp,
        viewportWidth = 17f,
        viewportHeight = 17f,
    ).group {
        path(
            fill = SolidColor(Color(0xFF949494)),
        ) {
            moveTo(2.83171f, 14.1673f)
            curveTo(2.44212f, 14.1673f, 2.10862f, 14.0286f, 1.83119f, 13.7512f)
            curveTo(1.55375f, 13.4737f, 1.41504f, 13.1402f, 1.41504f, 12.7507f)
            verticalLineTo(4.25065f)
            curveTo(1.41504f, 3.86107f, 1.55375f, 3.52756f, 1.83119f, 3.25013f)
            curveTo(2.10862f, 2.9727f, 2.44212f, 2.83398f, 2.83171f, 2.83398f)
            horizontalLineTo(6.49733f)
            curveTo(6.68622f, 2.83398f, 6.86626f, 2.8694f, 7.03744f, 2.94023f)
            curveTo(7.20862f, 3.01107f, 7.35914f, 3.11141f, 7.489f, 3.24128f)
            lineTo(8.49837f, 4.25065f)
            horizontalLineTo(14.165f)
            curveTo(14.5546f, 4.25065f, 14.8881f, 4.38937f, 15.1656f, 4.6668f)
            curveTo(15.443f, 4.94423f, 15.5817f, 5.27773f, 15.5817f, 5.66732f)
            verticalLineTo(12.7507f)
            curveTo(15.5817f, 13.1402f, 15.443f, 13.4737f, 15.1656f, 13.7512f)
            curveTo(14.8881f, 14.0286f, 14.5546f, 14.1673f, 14.165f, 14.1673f)
            horizontalLineTo(2.83171f)
            close()
        }
    }.build()
