package com.c242_ps009.habitsaga.ui.task

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.setupActionBarWithNavController
import com.c242_ps009.habitsaga.databinding.ActivityAddTaskBinding
import com.c242_ps009.habitsaga.ui.utils.DatePickerUtil
import com.google.firebase.auth.FirebaseAuth

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var binding: ActivityAddTaskBinding
    private var selectedCategory: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        setupSpinner()

        binding.apply {

            icCalendar.setOnClickListener {
                DatePickerUtil.showDatePickerDialog(this@AddTaskActivity, binding.etDate) { selectedDate ->
                   etDate.setText(selectedDate)
                }
            }

            etDate.setOnClickListener {
                DatePickerUtil.showDatePickerDialog(this@AddTaskActivity, binding.etDate) { selectedDate ->
                    etDate.setText(selectedDate)
                }
            }

            btnAddTask.setOnClickListener {
                val user = FirebaseAuth.getInstance().currentUser
                val userId = user?.uid ?: return@setOnClickListener
                val title = etTitle.text.toString()
                val description = etDescription.text.toString()
                val dueDate = etDate.text.toString()  // Use the updated date from EditText
                val category = spinCategory.selectedItem.toString()
                taskViewModel.addNewTask(userId, title, description, dueDate, category, priority = 1, isCompleted = false)
                finish()
            }

            btnCancel.setOnClickListener {
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupSpinner() {
        val category = arrayOf("Homework", "Work", "Personal", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, category)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinCategory.adapter = adapter

        binding.spinCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = position + 1
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
