

package com.example.todoapp.Adapter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.AddNewTask
import com.example.todoapp.MainActivity
import com.example.todoapp.Model.ToDoModel
import com.example.todoapp.R
import com.example.todoapp.Utils.DatabaseHandler
import com.google.android.material.chip.Chip

class ToDoAdapter(private val db: DatabaseHandler, private val activity: MainActivity) : RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {

    private var todoList: MutableList<ToDoModel> = mutableListOf()
    init {
        db.openDatabase()  // Opening the database once
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = todoList[position]
        holder.task.text = item.task
        holder.task.isChecked = toBoolean(item.status)
//        setPriorityBackground(holder, item.priority)

        holder.task.setOnCheckedChangeListener { _, isChecked ->
            db.updateStatus(item.id, if (isChecked) 1 else 0)
        }
        holder.deleteButton.setOnClickListener {
            val builder= AlertDialog.Builder(activity)
            builder.setTitle("Delete Tasks")
            builder.setMessage("Are you sure you want to delete this Task?")
            builder.setPositiveButton("Confirm") { _, _ ->
                deleteItem(position)
            }
            builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }
        holder.editButton.setOnClickListener {
            editItem(position)
        }

        // Display priority as text
        holder.priorityChip.text = getPriorityText(item.priority)

        val priorityColor = getPriorityColor(item.priority)
        holder.priorityChip.chipBackgroundColor = ContextCompat.getColorStateList(activity, priorityColor)
    }
    private fun getPriorityColor(priority: Int): Int {
        return when (priority) {
            2 -> R.color.colorHigh // Replace with your color for High priority
            1 -> R.color.colorMedium // Replace with your color for Medium priority
            else -> R.color.colorLow // Replace with your color for Low priority
        }
    }

    private fun toBoolean(n: Int): Boolean {
        return n != 0
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun getContext(): Context {
        return activity
    }

    fun setTasks(todoList: List<ToDoModel>) {
        this.todoList = todoList.toMutableList()
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        val item = todoList[position]
        db.deleteTask(item.id)
        todoList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int) {
        val item = todoList[position]
        val bundle = Bundle().apply {
            putInt("id", item.id)
            putString("task", item.task)
            putInt("priority", item.priority)
        }
        val fragment = AddNewTask().apply {
            arguments = bundle
        }
        fragment.show(activity.supportFragmentManager, AddNewTask.TAG)
    }

    private fun getPriorityText(priority: Int): String {
        return when (priority) {
            2 -> "High"
            1 -> "Medium"
            else -> "Low"
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val task: CheckBox = view.findViewById(R.id.tocheckbox)
        val deleteButton: ImageButton = view.findViewById(R.id.btndelete)
        val editButton: ImageButton = view.findViewById(R.id.btnedit)
        val priorityChip: Chip = view.findViewById(R.id.priority_chip)
    }
}
