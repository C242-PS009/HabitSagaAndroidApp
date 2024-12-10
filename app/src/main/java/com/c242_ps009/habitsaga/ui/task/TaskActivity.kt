package com.c242_ps009.habitsaga.ui.task

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.c242_ps009.habitsaga.databinding.ActivityTaskBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import android.widget.Toast
import com.c242_ps009.habitsaga.data.retrofit.ApiConfig
import com.c242_ps009.habitsaga.data.retrofit.TaskRequest
import com.c242_ps009.habitsaga.data.retrofit.TaskResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

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
            taskViewModel.fetchAndProcessTasks()
        }

//        binding.btnDelete.setOnClickListener {
//            taskViewModel.deleteAllTasks()
//        }

        binding.btnDone.setOnClickListener {
            val selectedTasks = taskAdapter.getSelectedTasks()
            if (selectedTasks.isNotEmpty()) {
                markTasksAsDone(selectedTasks)
            } else {
                Snackbar.make(binding.root, "No tasks selected", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Example task titles
//        val tasks = listOf("Rewatch Shoujo Ramune", "Ganyang fufufafa")
//        val request = TaskRequest(tasks)
//
//        // Make the API call
//        ApiConfig.apiService().getTaskPriorities(request).enqueue(object : Callback<TaskResponse> {
//            override fun onResponse(
//                call: Call<TaskResponse>,
//                response: Response<TaskResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val labels = response.body()?.labels
//                    labels?.let {
//                        Toast.makeText(this@TaskActivity, "Predictions: $it", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(this@TaskActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<TaskResponse>, t: Throwable) {
//                Toast.makeText(this@TaskActivity, "Failed: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
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
