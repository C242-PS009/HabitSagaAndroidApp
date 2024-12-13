package com.c242_ps009.habitsaga.ui.statistic

import android.graphics.Color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

class BarChartDaily {

    fun setupBarChart(barChart: BarChart, data: List<Int>) {
        barChart.setBackgroundColor(Color.TRANSPARENT)
        barChart.description.isEnabled = false

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.textSize = 12f
        xAxis.textColor = Color.WHITE
        xAxis.labelRotationAngle = 0f

        val yAxis = barChart.axisLeft
        yAxis.setDrawLabels(true)
        yAxis.setDrawGridLines(true)
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 20f
        yAxis.granularity = 1f
        yAxis.textColor = Color.WHITE

        barChart.axisRight.isEnabled = false

        val legend = barChart.legend
        legend.textColor = Color.WHITE

        barChart.setExtraOffsets(0f, 0f, 0f, 20f)

        val entries = ArrayList<BarEntry>()
        data.forEachIndexed { index, count ->
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
        }

        val barDataSet = BarDataSet(entries, "Tasks Completed")
        barDataSet.color = Color.parseColor("#3737D2")
        barDataSet.valueTextColor = Color.WHITE
        barDataSet.valueTextSize = 10f
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                return "${barEntry?.y?.toInt()} task"
            }
        }

        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.invalidate()
    }
}