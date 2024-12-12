package com.c242_ps009.habitsaga.ui.statistic

import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class BarChartPriority {

    fun setupBarChart(barChart: BarChart, data: List<BarEntry>) {
        barChart.apply {
            setBackgroundColor(Color.TRANSPARENT)
            description.isEnabled = false
            axisRight.isEnabled = false
            setExtraOffsets(10f, 10f, 10f, 30f)
        }

        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(listOf(
                "urgent important", "urgent not-important", "not-urgent important", "not-urgent not-important"
            ))
            granularity = 1f
            setDrawGridLines(false)
            setDrawAxisLine(false)
            textSize = 12f
            textColor = Color.WHITE
            labelRotationAngle = 45f  // Set the orientation to 45 degrees
        }

        barChart.axisLeft.apply {
            setDrawLabels(true)
            setDrawGridLines(true)
            axisMinimum = 0f
            axisMaximum = data.maxOf { it.y } + 1
            granularity = 1f
            textColor = Color.WHITE
        }

        barChart.legend.textColor = Color.WHITE

        val urgentImportant = data.filter { it.x == 0f }
        val urgentNotImportant = data.filter { it.x == 1f }
        val notUrgentImportant = data.filter { it.x == 2f }
        val notUrgentNotImportant = data.filter { it.x == 3f }

        val barDataSets = listOf(
            createBarDataSet(urgentImportant, "Urgent Important", Color.RED),
            createBarDataSet(urgentNotImportant, "Urgent Not-important", Color.YELLOW),
            createBarDataSet(notUrgentImportant, "Not-urgent Important", Color.BLUE),
            createBarDataSet(notUrgentNotImportant, "Not-urgent Not-important", Color.GREEN)
        )

        barChart.data = BarData(barDataSets)
        barChart.invalidate()
    }

    private fun createBarDataSet(entries: List<BarEntry>, label: String, color: Int): BarDataSet {
        return BarDataSet(entries, label).apply {
            setColor(color)
            valueTextColor = Color.WHITE
            valueTextSize = 10f
            valueFormatter = object : ValueFormatter() {
                override fun getBarLabel(barEntry: BarEntry?): String {
                    return "${barEntry?.y?.toInt()}"
                }
            }
        }
    }
}
