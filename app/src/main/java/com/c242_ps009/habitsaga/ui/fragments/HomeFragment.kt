package com.c242_ps009.habitsaga.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.c242_ps009.habitsaga.databinding.FragmentHomeBinding
import com.c242_ps009.habitsaga.ui.pomodoro.PomodoroActivity
import com.c242_ps009.habitsaga.ui.task.TaskActivity


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

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
                val intent = Intent(context, PomodoroActivity::class.java)
                startActivity(intent)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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