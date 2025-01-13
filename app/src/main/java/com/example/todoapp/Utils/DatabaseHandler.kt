//package com.example.todoapp.Utils
//
//import android.annotation.SuppressLint
//import android.content.ContentValues
//import android.content.Context
//import android.database.Cursor
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import com.example.todoapp.Model.ToDoModel
//
//class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
//
//    private var db: SQLiteDatabase? = null
//
//    companion object {
//        private const val DATABASE_VERSION = 1
//        private const val DATABASE_NAME = "toDoListDatabase"
//        private const val TABLE_TODO = "todo"
//        private const val COLUMN_ID = "id"
//        private const val COLUMN_TASK = "task"
//        private const val COLUMN_STATUS = "status"
//        private const val COLUMN_PRIORITY=0
//        private const val CREATE_TODO_TABLE = "CREATE TABLE $TABLE_TODO ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TASK TEXT, $COLUMN_STATUS INTEGER,$COLUMN_PRIORITY INTEGER)"
//    }
//
////    private var db: SQLiteDatabase by lazy { this.writableDatabase }
//
//    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL(CREATE_TODO_TABLE)
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db.execSQL("DROP TABLE IF EXISTS $TABLE_TODO")
//        onCreate(db)
//    }
//
//    fun openDatabase() {
//        db = this.writableDatabase
//    }
//
//
//    fun insertTask(task: ToDoModel) {
//        val cv = ContentValues()
//        cv.put(COLUMN_TASK, task.task)
//        cv.put(COLUMN_STATUS, 0)
//        cv.put(COLUMN_PRIORITY,0)
//        db?.insert(TABLE_TODO, null, cv)
//    }
//
//    @SuppressLint("Range")
//    fun getAllTasks(): List<ToDoModel> {
//        val taskList = mutableListOf<ToDoModel>()
//        val cursor: Cursor? = db?.query(TABLE_TODO, null, null, null, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                do {
//                    val task = ToDoModel().apply {
//                        id = it.getInt(it.getColumnIndex(COLUMN_ID))
//                        task = it.getString(it.getColumnIndex(COLUMN_TASK))
//                        status = it.getInt(it.getColumnIndex(COLUMN_STATUS))
//                    }
//                    taskList.add(task)
//                } while (it.moveToNext())
//            }
//        }
//        return taskList
//    }
//
//    fun updateStatus(id: Int, status: Int) {
//        val cv = ContentValues()
//        cv.put(COLUMN_STATUS, status)
//        db?.update(TABLE_TODO, cv, "$COLUMN_ID = ?", arrayOf(id.toString()))
//    }
//
//    fun updateTask(id: Int, task: String) {
//        val cv = ContentValues()
//        cv.put(COLUMN_TASK, task)
//        db?.update(TABLE_TODO, cv, "$COLUMN_ID = ?", arrayOf(id.toString()))
//    }
//
//    fun deleteTask(id: Int) {
//        db?.delete(TABLE_TODO, "$COLUMN_ID = ?", arrayOf(id.toString()))
//    }
//}

package com.example.todoapp.Utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.todoapp.Model.ToDoModel

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var db: SQLiteDatabase? = null

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "toDoListDatabase"
        private const val TABLE_TODO = "todo"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TASK = "task"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_PRIORITY = "priority" // Corrected: Column name for priority
        private const val CREATE_TODO_TABLE = "CREATE TABLE $TABLE_TODO ("+
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "$COLUMN_TASK TEXT, "+
                "$COLUMN_STATUS INTEGER, "+
                "$COLUMN_PRIORITY INTEGER DEFAULT 1)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TODO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db.execSQL("DROP TABLE IF EXISTS $TABLE_TODO")
//        onCreate(db)
        // Handle database upgrade to add the priority column
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_TODO ADD COLUMN $COLUMN_PRIORITY INTEGER DEFAULT 0")
        }
    }

    fun openDatabase() {
        db = this.writableDatabase
    }
//    fun logAllTasks(databaseHandler: DatabaseHandler) {
//        // Open the database
//        databaseHandler.openDatabase()
//
//        // Retrieve all tasks from the database
//        val tasks = databaseHandler.getAllTasks()
//
//        // Log each task
//        for (task in tasks) {
//            Log.d("DatabaseLog", "ID: ${task.id}, Task: ${task.task}, Status: ${task.status}, Priority: ${task.priority}")
//        }
//    }
    // Insert task into the database
    fun insertTask(task: ToDoModel) {
        val cv = ContentValues()
        cv.put(COLUMN_TASK, task.task)
        cv.put(COLUMN_STATUS, task.status)  // Updated to use the task status
        cv.put(COLUMN_PRIORITY, task.priority) // Updated to use the task priority
        db?.insert(TABLE_TODO, null, cv)
    }

    // Retrieve all tasks from the database
    @SuppressLint("Range")
    fun getAllTasks(): List<ToDoModel> {
        val taskList = mutableListOf<ToDoModel>()
        val cursor: Cursor? = db?.query(TABLE_TODO, null, null, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                do {
                    val task = ToDoModel().apply {
                        id = it.getInt(it.getColumnIndex(COLUMN_ID))
                        task = it.getString(it.getColumnIndex(COLUMN_TASK))
                        status = it.getInt(it.getColumnIndex(COLUMN_STATUS))
                        priority = it.getInt(it.getColumnIndex(COLUMN_PRIORITY)) // Retrieve priority
                    }
                    taskList.add(task)
                } while (it.moveToNext())
            }
        }
        return taskList
    }

    // Update task status in the database
    fun updateStatus(id: Int, status: Int) {
        val cv = ContentValues()
        cv.put(COLUMN_STATUS, status)
        db?.update(TABLE_TODO, cv, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Update task text in the database
    fun updateTask(id: Int, task: String) {
        val cv = ContentValues()
        cv.put(COLUMN_TASK, task)
        db?.update(TABLE_TODO, cv, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Update task priority in the database
    fun updatePriority(id: Int, priority: Int) {
        val cv = ContentValues()
        cv.put(COLUMN_PRIORITY, priority)
        db?.update(TABLE_TODO, cv, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Delete a task from the database
    fun deleteTask(id: Int) {
        db?.delete(TABLE_TODO, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}
