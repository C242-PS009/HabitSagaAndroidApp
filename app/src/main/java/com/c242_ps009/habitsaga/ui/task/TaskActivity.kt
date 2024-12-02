package com.c242_ps009.habitsaga.ui.task

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.c242_ps009.habitsaga.databinding.ActivityTaskBinding
import com.google.android.material.snackbar.Snackbar

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter()
        binding.rvTasks.adapter = taskAdapter

        taskViewModel.tasksLiveData.observe(this) { tasks ->
            if (tasks.isNullOrEmpty()) {
                Snackbar.make(binding.root, "No tasks found", Snackbar.LENGTH_SHORT).show()
            } else {
                taskAdapter.submitList(tasks)
            }
        }

        taskViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
        }

        taskViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, "Error: $it", Snackbar.LENGTH_LONG).show()
            }
        }

        binding.cvAddTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        taskViewModel.listenerRegistration?.remove()
    }
}
