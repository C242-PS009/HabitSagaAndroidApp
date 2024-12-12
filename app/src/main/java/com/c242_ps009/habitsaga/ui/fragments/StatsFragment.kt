package com.c242_ps009.habitsaga.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.c242_ps009.habitsaga.databinding.FragmentStatsBinding
import com.c242_ps009.habitsaga.ui.statistic.BarChartDaily
import com.c242_ps009.habitsaga.ui.statistic.BarChartPriority
import com.c242_ps009.habitsaga.ui.statistic.StatsViewModel
import com.github.mikephil.charting.data.BarEntry

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by viewModels()
    private val chartDaily = BarChartDaily()
    private val chartPriority = BarChartPriority()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskDaily.observe(viewLifecycleOwner) { taskDaily ->
            chartDaily.setupBarChart(binding.barChartDaily, taskDaily)
        }

        viewModel.fetchTaskDaily()

        viewModel.priorityCounts.observe(viewLifecycleOwner) { priorityCounts ->
            val data = priorityCounts.entries.mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.value.toFloat())
            }
            chartPriority.setupBarChart(binding.barChartPriority, data)
        }

        viewModel.fetchPriorityCounts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}