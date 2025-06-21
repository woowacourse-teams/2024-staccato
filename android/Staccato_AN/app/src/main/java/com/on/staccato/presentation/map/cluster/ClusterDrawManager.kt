package com.on.staccato.presentation.map.cluster

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.on.staccato.R
import com.on.staccato.presentation.map.model.ClusterColor
import javax.inject.Inject

class ClusterDrawManager
    @Inject
    constructor(private val context: Context) {
        fun generateClusterIcon(staccatoCount: Int): BitmapDescriptor {
            val bitmap: Bitmap = Bitmap.createBitmap(BITMAP_SIZE, BITMAP_SIZE, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val clusterColor: ClusterColor = getClusterColor(staccatoCount)

            drawClusterBackground(clusterColor, canvas)
            drawStaccatoCount(canvas, staccatoCount)

            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }

        private fun getClusterColor(clusterSize: Int) =
            when (clusterSize) {
                in CLUSTER_SMALL -> ClusterColor.MINT
                in CLUSTER_MEDIUM -> ClusterColor.BLUE
                else -> ClusterColor.INDIGO
            }

        private fun drawClusterBackground(
            clusterColor: ClusterColor,
            canvas: Canvas,
        ) {
            val innerRadiusRatio = calculateInnerRadiusRatio()
            val innerRadius = OUTER_RADIUS * innerRadiusRatio
            val outerPaint: Paint = createBackgroundPaint(clusterColor.outerCircle)
            val innerPaint: Paint = createBackgroundPaint(clusterColor.innerCircle)

            canvas.drawCircle(OUTER_RADIUS, OUTER_RADIUS, OUTER_RADIUS, outerPaint)
            canvas.drawCircle(OUTER_RADIUS, OUTER_RADIUS, innerRadius, innerPaint)
        }

        private fun drawStaccatoCount(
            canvas: Canvas,
            staccatoCount: Int,
        ) {
            val textPaint = createTextPaint()
            val textY = (OUTER_RADIUS - (textPaint.descent() + textPaint.ascent()) / 2)
            canvas.drawText(context.getString(R.string.cluster_staccato_count, staccatoCount), OUTER_RADIUS, textY, textPaint)
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
                textSize = 38f
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
                isAntiAlias = true
            }

        private fun calculateInnerRadiusRatio() = (PI * INNER_RADIUS * INNER_RADIUS) / (PI * OUTER_RADIUS * OUTER_RADIUS)

        companion object {
            private val CLUSTER_SMALL = 2..9
            private val CLUSTER_MEDIUM = 10..99
            private const val BITMAP_SIZE = 160
            private const val OUTER_RADIUS = BITMAP_SIZE / 2.0f
            private const val INNER_RADIUS = 73
            private const val PI = 3.14f
        }
    }
