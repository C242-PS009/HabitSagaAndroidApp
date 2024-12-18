package com.c242_ps009.habitsaga.ui.task

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c242_ps009.habitsaga.databinding.ActivityTaskBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter()
        binding.rvTasks.adapter = taskAdapter

        taskViewModel.tasksLiveData.observe(this) { tasks ->
            taskAdapter.submitList(tasks)
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

        binding.btnSortTasks.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            lifecycleScope.launch {
                taskViewModel.fetchAndProcessTasks()
                binding.progressBar.visibility = View.GONE
            }
        }

        binding.btnDone.setOnClickListener {
            val selectedTasks = taskAdapter.getSelectedTasks()
            binding.progressBar.visibility = View.VISIBLE
            if (selectedTasks.isNotEmpty()) {
                lifecycleScope.launch {
                    markTasksAsDone(selectedTasks)
                    binding.progressBar.visibility = View.GONE
                }
            } else {
                Snackbar.make(binding.root, "No tasks selected", Snackbar.LENGTH_SHORT).show()
            }
        }
   }

    private fun markTasksAsDone(tasks: Set<Task>) {
        lifecycleScope.launch {
            tasks.forEach { task ->
                task.isCompleted = true
                taskViewModel.markTaskCompleted(task.id)
            }
            taskAdapter.clearSelection()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
