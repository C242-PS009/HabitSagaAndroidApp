package com.c242_ps009.habitsaga.ui.statistic

import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter

class PieChart {

    fun setupPieChart(pieChart: PieChart, entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "Tasks").apply {
            colors = listOf(
                Color.parseColor(COLOR_COMPLETED),
                Color.parseColor(COLOR_INCOMPLETE)
            )
            setHighlightEnabled(true)
            selectionShift = 5f
            valueFormatter = DefaultValueFormatter(0)
            valueTextColor = Color.WHITE
            valueTextSize = 10f
        }

        pieChart.apply {
            data = PieData(dataSet)
            setUsePercentValues(false)
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 35f
            transparentCircleRadius = 70f
            setHoleColor(Color.WHITE)
            setCenterTextColor(Color.WHITE)
            setDrawEntryLabels(true)
            setEntryLabelTextSize(10f)
            setEntryLabelColor(Color.WHITE)

            legend.apply {
                isEnabled = true
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                setTextColor(Color.WHITE)
            }

            animateXY(1400, 1400)
            invalidate()
        }
    }

    companion object {
        private const val COLOR_INCOMPLETE = "#FFC107"
        private const val COLOR_COMPLETED = "#4CAF50"
    }
}