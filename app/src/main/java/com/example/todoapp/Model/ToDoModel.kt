package com.example.todoapp.Model

data class ToDoModel(
    var id: Int = 0,
    var task: String = "",
    var status: Int = 0,
    var priority: Int = 0 // Use Int instead of String for priority
)
