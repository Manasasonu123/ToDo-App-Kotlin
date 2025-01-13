package com.example.todoapp

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.Adapter.ToDoAdapter
import com.example.todoapp.AddNewTask
import com.example.todoapp.DialogCloseListener
import com.example.todoapp.Model.ToDoModel
import com.example.todoapp.R
import com.example.todoapp.RecyclerItemTouchHelper
import com.example.todoapp.Utils.DatabaseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton


import java.util.*

class MainActivity : AppCompatActivity(), DialogCloseListener {

    private lateinit var db: DatabaseHandler
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var tasksAdapter: ToDoAdapter
    private lateinit var fab: FloatingActionButton
    private var taskList: MutableList<ToDoModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        //supportActionBar?.title="My To-Do List"

        db = DatabaseHandler(this)
        db.openDatabase()

        // Fetch all tasks from the database
        taskList = db.getAllTasks().toMutableList()

        // Log the tasks to check if they are fetched correctly
        logTasks(taskList)

        tasksRecyclerView = findViewById(R.id.recyclerview)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)
        tasksAdapter = ToDoAdapter(db, this)
        tasksRecyclerView.adapter = tasksAdapter

        //attaches swipe function to recycler view
        val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(tasksAdapter))
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)



        fab = findViewById(R.id.fab)

        taskList = db.getAllTasks().toMutableList()
        taskList.reverse()

        tasksAdapter.setTasks(taskList)

        fab.setOnClickListener {
            AddNewTask.newInstance().show(supportFragmentManager, AddNewTask.TAG)
        }
        registerForContextMenu(tasksRecyclerView)
    }

    override fun handleDialogClose(dialog: DialogInterface) {
        taskList = db.getAllTasks().toMutableList()
        taskList.reverse()
        tasksAdapter.setTasks(taskList)
        tasksAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.context_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_about-> {
                showAboutDialog()
                return true;
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showAboutDialog(){
        val builder=AlertDialog.Builder(this)
        builder.setTitle("About")
            .setMessage("This is a simple To-Do app.\n\nVersion: 1.0\nDeveloped by: Manasa")
            .setPositiveButton("OK"){ dialog,_->
                dialog.dismiss()
            }
        val dialog=builder.create()
        dialog.show()
    }

    private fun logTasks(tasks: List<ToDoModel>) {
        for (task in tasks) {
            Log.d("TaskLog", "ID: ${task.id}, Task: ${task.task}, Status: ${task.status}, Priority: ${task.priority}")
        }
    }
}
