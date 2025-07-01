package com.on.staccato.presentation.map.cluster

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.on.staccato.presentation.R
import com.on.staccato.presentation.map.model.ClusterColor
import javax.inject.Inject

class ClusterDrawManager
    @Inject
    constructor(private val context: Context) {
        fun generateClusterIcon(staccatoCount: Int): BitmapDescriptor {
            val bitmap: Bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val clusterColor: ClusterColor = getClusterColor(staccatoCount)

            canvas.drawClusterBackground(clusterColor)
            canvas.drawStaccatoCount(staccatoCount)

            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

        private fun getClusterColor(clusterSize: Int) =
            when (clusterSize) {
                in CLUSTER_SMALL -> ClusterColor.MINT
                in CLUSTER_MEDIUM -> ClusterColor.BLUE
                else -> ClusterColor.INDIGO
            }

        private fun Canvas.drawClusterBackground(clusterColor: ClusterColor) {
            val innerRadius = OUTER_RADIUS * INNER_RADIUS_RATIO
            val outerPaint: Paint = createBackgroundPaint(clusterColor.outerCircle)
            val innerPaint: Paint = createBackgroundPaint(clusterColor.innerCircle)

            drawCircle(OUTER_RADIUS, OUTER_RADIUS, OUTER_RADIUS, outerPaint)
            drawCircle(OUTER_RADIUS, OUTER_RADIUS, innerRadius, innerPaint)
        }

        private fun Canvas.drawStaccatoCount(staccatoCount: Int) {
            val textPaint = createTextPaint()
            val textY = (OUTER_RADIUS - (textPaint.descent() + textPaint.ascent()) / 2)
            drawText(context.getString(R.string.cluster_staccato_count, staccatoCount), OUTER_RADIUS, textY, textPaint)
        }

        private fun createBackgroundPaint(
            @ColorRes colorId: Int,
        ): Paint =
            Paint().apply {
                color = ContextCompat.getColor(context, colorId)
                isAntiAlias = true
                style = Paint.Style.FILL
            }

        private fun createTextPaint(): Paint =
            Paint().apply {
                color = Color.WHITE
                textSize = 44f
                textAlign = Paint.Align.CENTER
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_medium)
                isAntiAlias = true
            }

        companion object {
            private val CLUSTER_SMALL = 2..9
            private val CLUSTER_MEDIUM = 10..99
            private const val BITMAP_SIZE = 160
            private const val OUTER_RADIUS = BITMAP_SIZE / 2.0f
            private const val INNER_RADIUS = 73
            private const val PI = 3.14f
            private const val INNER_RADIUS_RATIO = (PI * INNER_RADIUS * INNER_RADIUS) / (PI * OUTER_RADIUS * OUTER_RADIUS)
        }
    }
