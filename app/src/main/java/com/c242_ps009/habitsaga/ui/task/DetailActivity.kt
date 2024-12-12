package com.c242_ps009.habitsaga.ui.task

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.c242_ps009.habitsaga.R
import com.c242_ps009.habitsaga.databinding.ActivityDetailBinding
import com.c242_ps009.habitsaga.utils.DatePickerUtil
import kotlinx.coroutines.launch
import java.util.Date

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private var updatedDueDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getStringExtra("documentId")
        val taskTitle = intent.getStringExtra("taskTitle") ?: "No Title"
        val taskDescription = intent.getStringExtra("taskDescription") ?: "No Description"
        val taskDueDate = intent.getStringExtra("taskDueDate") ?: "No Due Date"
        val taskCategory = intent.getStringExtra("taskCategory") ?: "No Category"

        binding.etTitle.text = Editable.Factory.getInstance().newEditable(taskTitle)
        binding.etDescription.text = Editable.Factory.getInstance().newEditable(taskDescription)
        binding.etDate.text = Editable.Factory.getInstance().newEditable(taskDueDate)
        binding.etCategory.text = Editable.Factory.getInstance().newEditable(taskCategory)

        updatedDueDate = parseDate(taskDueDate)

        binding.apply {

            icCalendar.setOnClickListener {
                DatePickerUtil.showDatePickerDialog(this@DetailActivity, binding.etDate) { selectedDate ->
                    binding.etCategory.text = Editable.Factory.getInstance().newEditable(selectedDate)
                    updatedDueDate = parseDate(selectedDate)
                }
            }

            btnEdit.setOnClickListener {
                val updatedTitle = etTitle.text.toString().trim()
                val updatedDescription = etDescription.text.toString().trim()
                val taskDueDateToUpdate = updatedDueDate ?: parseDate(taskDueDate) ?: Date()
                val updatedTaskCategory = etCategory.text.toString().trim()

                if (title.isNotEmpty()) {
                    id?.let {
                        val updatedTask = Task(
                            title = updatedTitle,
                            description = updatedDescription,
                            dueDate = taskDueDateToUpdate,
                            category = updatedTaskCategory
                        )
                        taskViewModel.updateTask(it, updatedTask)
                        finish()
                    }
                } else {
                    Toast.makeText(this@DetailActivity, "Please select a priority", Toast.LENGTH_SHORT).show()
                }

                id?.let {
                    val updatedTask = Task(
                        title = updatedTitle,
                        description = updatedDescription,
                        dueDate = taskDueDateToUpdate,
                        category = updatedTaskCategory
                    )
                    taskViewModel.updateTask(it, updatedTask)
                    finish()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                val id = intent.getStringExtra("documentId")
                id?.let {
                    taskViewModel.deleteTask(it)
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun parseDate(dateString: String): Date? {
        return try {
            val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US)
            dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            Log.e("DetailActivity", "Error parsing date: ${e.message}")
            null
        }
    }
}