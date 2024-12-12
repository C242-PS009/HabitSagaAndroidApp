package com.c242_ps009.habitsaga.ui.task

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.c242_ps009.habitsaga.databinding.ActivityDetailBinding
import com.c242_ps009.habitsaga.utils.DatePickerUtil
import kotlinx.coroutines.launch
import java.util.Date

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val taskViewModel: TaskViewModel by viewModels()
    private var updatedDueDate: Date? = null
    private var selectedCategory: Int = 1

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

        binding.etTaskTitle.text = Editable.Factory.getInstance().newEditable(taskTitle)
        binding.etTaskDescription.text = Editable.Factory.getInstance().newEditable(taskDescription)
        binding.tvTaskDueDate.text = taskDueDate
        binding.spinCategory.setSelection(getCategoryIndex(taskCategory))

        updatedDueDate = parseDate(taskDueDate)
        setupSpinner()

        binding.apply {

            icCalendar.setOnClickListener {
                DatePickerUtil.showDatePickerDialog(this@DetailActivity, binding.tvTaskDueDate) { selectedDate ->
                    binding.tvTaskDueDate.text = selectedDate
                    updatedDueDate = parseDate(selectedDate)
                }
            }

            icDelete.setOnClickListener {
                id?.let {
                    taskViewModel.deleteTask(it)
                }
                finish()
            }

            btnDone.setOnClickListener {
                id?.let {
                    lifecycleScope.launch {
                        taskViewModel.markTaskCompleted(it)
                        finish()
                    }
                }
            }


            btnEdit.setOnClickListener {
                val updatedTitle = etTaskTitle.text.toString().trim()
                val updatedDescription = etTaskDescription.text.toString().trim()
                val updatedCategory = binding.spinCategory.selectedItem.toString().trim()
                val taskDueDateToUpdate = updatedDueDate ?: parseDate(taskDueDate) ?: Date()

                id?.let {
                    val updatedTask = Task(
                        title = updatedTitle,
                        description = updatedDescription,
                        dueDate = taskDueDateToUpdate,
                        category = updatedCategory
                    )
                    taskViewModel.updateTask(it, updatedTask)
                    finish()
                }
            }
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

    private fun setupSpinner() {
        val priorities = arrayOf("Homework", "Work", "Personal", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinCategory.adapter = adapter

        binding.spinCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = position + 1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun getCategoryIndex(category: String): Int {
        return when (category) {
            "Homework" -> 0
            "Work" -> 1
            "Personal" -> 2
            "Other" -> 3
            else -> 0
        }
    }
}