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

    private val selectedTasks = mutableSetOf<Task>()

    fun getSelectedTasks(): Set<Task> = selectedTasks
    fun clearSelection() {
        selectedTasks.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task) { clickedTask, isChecked ->
            if (isChecked) {
                selectedTasks.add(clickedTask)
            } else {
                selectedTasks.remove(clickedTask)
            }
        }
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task, onClick: (Task, Boolean) -> Unit) {
            binding.tvTitle.text = task.title
            binding.cbTaskSelect.isChecked = selectedTasks.contains(task)

            binding.cbTaskSelect.setOnCheckedChangeListener(null)
            binding.cbTaskSelect.setOnCheckedChangeListener { _, isChecked ->
                onClick(task, isChecked)
            }

            binding.root.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("documentId", task.id)
                    putExtra("taskTitle", task.title)
                    putExtra("taskDescription", task.description)
                    putExtra("taskDueDate", SimpleDateFormat("dd/MM/yyyy", Locale.US).format(task.dueDate))
                    putExtra("taskCategory", task.category)
                    putExtra("taskPriority", task.priority)
                }
                context.startActivity(intent)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}