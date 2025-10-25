package com.on.staccato.macrobenchmark

import android.annotation.SuppressLint
import android.content.Intent
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CategoriesScrollBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun scrollCategoriesCompilationFull() = scrollCategories(CompilationMode.Full())

    @SuppressLint("SuspiciousIndentation")
    @OptIn(ExperimentalMetricApi::class)
    private fun scrollCategories(compilationMode: CompilationMode) =
        benchmarkRule.measureRepeated(
            packageName = "com.on.staccato",
            compilationMode = compilationMode,
            metrics =
                listOf(
                    FrameTimingMetric(),
                    TraceSectionMetric("CategoryItem"),
                    TraceSectionMetric("ThumbnailImage"),
                    TraceSectionMetric("FirstSpacer"),
                    TraceSectionMetric("CategoryFolder"),
                    TraceSectionMetric("SecondSpacer"),
                    TraceSectionMetric("Title"),
                    TraceSectionMetric("CategoryPeriod"),
                    TraceSectionMetric("ThirdSpacer"),
                    TraceSectionMetric("Participants"),
                    TraceSectionMetric("ParticipantItem"),
                    TraceSectionMetric("StaccatoCount"),
                ),
            iterations = 5,
            startupMode = StartupMode.WARM,
            setupBlock = {
                pressHome()
                val intent =
                    Intent().apply {
                        setClassName(
                            "com.on.staccato",
                            "com.on.staccato.benchmark.CategoriesBenchmarkActivity",
                        )
                    }
                startActivityAndWait(intent)
            },
        ) {
            val categories =
                device.wait(
                    Until.findObject(By.res("categories_lazy_column")),
                    1_000,
                ) ?: error("카테고리 Lazy Column을 찾을 수 없음")

            repeat(2) {
                categories.fling(Direction.DOWN)
                categories.fling(Direction.UP)
            }
        }
}
