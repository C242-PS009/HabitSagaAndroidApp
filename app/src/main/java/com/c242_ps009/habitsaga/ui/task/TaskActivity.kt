package com.c242_ps009.habitsaga.ui.task

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.c242_ps009.habitsaga.databinding.ActivityTaskBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ListenerRegistration

class TaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    private var listenerRegistration: ListenerRegistration? = null

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

    /*
        You know, Firestore handles the removal under the hood... but hey, I’m just here
        to make sure it's removed manually too, because memory leaks are a big no-no,
        and who doesn't love being extra cautious? Better safe than sorry!
    */
    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.let { TaskRepository().removeListener(it) }
    }
}
