package com.c242_ps009.habitsaga.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.c242_ps009.habitsaga.R
import com.c242_ps009.habitsaga.databinding.FragmentHomeBinding
import com.c242_ps009.habitsaga.ui.gamification.UserViewModel
import com.c242_ps009.habitsaga.ui.statistic.PieChart
import com.c242_ps009.habitsaga.ui.statistic.StatsViewModel
import com.c242_ps009.habitsaga.ui.task.TaskActivity
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val viewModel: UserViewModel by viewModels()
    private val statsViewModel: StatsViewModel by viewModels()
    private val pieChart = PieChart()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel.userData.observe(viewLifecycleOwner, { user ->
            user?.let {
                binding.tvUsername.text = it.name
                binding.tvLevel.text = "Level ${it.level}"
                binding.pbLevel.progress = it.expProgress
            }
        })

        statsViewModel.taskCounts.observe(viewLifecycleOwner, { taskCounts ->
            taskCounts?.let {
                val (completed, incomplete) = it
                val entries = listOf(
                    PieEntry(completed.toFloat(), getString(R.string.completed)),
                    PieEntry(incomplete.toFloat(), getString(R.string.incomplete))
                )
                pieChart.setupPieChart(binding.pieChart, entries)
                binding.tvCompleted.text = getString(R.string.completed_task, completed)
                binding.tvIncomplete.text = getString(R.string.incomplete_task, incomplete)
                binding.progressBar2.visibility = View.GONE // Hide the progress bar
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                binding.progressBar2.visibility = View.GONE // Hide the progress bar on error
            }
        })

        val userId = auth.currentUser?.uid
        if (userId != null) {
            viewModel.fetchUserInfo(userId)
        } else {
            binding.tvUsername.text = "User not logged in"
            binding.tvLevel.text = "Level: N/A"
            binding.pbLevel.progress = 0
        }

        binding.mascot.apply {
            layer1 =
                "https://raw.githubusercontent.com/C242-PS009/assets/refs/heads/master/characters/orca/think.svg"
            layer2 =
                "https://raw.githubusercontent.com/C242-PS009/assets/refs/heads/master/equippables/glasses/think.svg"
        }

        binding.apply {
            cvTask.setOnClickListener {
                val intent = Intent(context, TaskActivity::class.java)
                startActivity(intent)
            }
            cvPomodoro.setOnClickListener {

            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statsViewModel.fetchTaskCounts()

        // Handle back press, close the app without going to the login screen
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}