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
            size: Int = BITMAP_SIZE,
        ) {
            val center = size / OUTER_RADIUS_DIVISOR
            val innerRadius = center * INNER_RADIUS_RATIO
            val outerPaint: Paint = createBackgroundPaint(clusterColor.outerCircle)
            val innerPaint: Paint = createBackgroundPaint(clusterColor.innerCircle)

            canvas.drawCircle(center, center, center, outerPaint)
            canvas.drawCircle(center, center, innerRadius, innerPaint)
        }

        private fun drawStaccatoCount(
            canvas: Canvas,
            staccatoCount: Int,
            center: Float = BITMAP_SIZE / OUTER_RADIUS_DIVISOR,
        ) {
            val textPaint = createTextPaint()
            val textY = (center - (textPaint.descent() + textPaint.ascent()) / 2)
            canvas.drawText("$staccatoCount", center, textY, textPaint)
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
                textSize = 40f
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
                isAntiAlias = true
            }

        companion object {
            private val CLUSTER_SMALL = 2..9
            private val CLUSTER_MEDIUM = 10..99
            private const val BITMAP_SIZE = 150
            private const val OUTER_RADIUS_DIVISOR = 2.0f
            private const val INNER_RADIUS_RATIO = 0.8f
        }
    }
