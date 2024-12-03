package com.c242_ps009.habitsaga.ui.task

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.c242_ps009.habitsaga.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task) { clickedTask ->
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("documentId", clickedTask.id)
                putExtra("taskTitle", clickedTask.title)
                putExtra("taskDescription", clickedTask.description)
                putExtra("taskDueDate", SimpleDateFormat("dd/MM/yyyy", Locale.US).format(clickedTask.dueDate))
                putExtra("taskCategory", clickedTask.category)
            }
            context.startActivity(intent)
        }
    }

    class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task, onClick: (Task) -> Unit) {
            binding.tvTitle.text = task.title
            binding.root.setOnClickListener {
                onClick(task)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.title == newItem.title && oldItem.dueDate == newItem.dueDate
        }
    }
}