package com.c242_ps009.habitsaga.ui.task

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.c242_ps009.habitsaga.R
import com.c242_ps009.habitsaga.databinding.ActivityAddTaskBinding
import com.c242_ps009.habitsaga.ui.utils.DatePickerUtil
import com.google.firebase.auth.FirebaseAuth

class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

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
                val dueDate = etDate.text.toString()
                val category = etCategory.text.toString()
                val priority = when (binding.rgPriority.checkedRadioButtonId) {
                    R.id.rb_1 -> "Urgent and Important"
                    R.id.rb_2 -> "Urgent but Not Important"
                    R.id.rb_3 -> "Not Urgent but Important"
                    R.id.rb_4 -> "Not Urgent and Not Important"
                    else -> ""
                }

                if (priority.isNotEmpty()) {
                    taskViewModel.addNewTask(userId, title, description, dueDate, category, priority ,isCompleted = false)
                    finish()
                } else {
                    Toast.makeText(this@AddTaskActivity, "Please select a priority", Toast.LENGTH_SHORT).show()
                }
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
}
