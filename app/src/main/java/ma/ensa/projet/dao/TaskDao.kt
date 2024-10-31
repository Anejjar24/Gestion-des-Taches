package ma.ensa.projet.dao

import ma.ensa.projet.beans.Task

interface TaskDao {
    fun getAllTasks(): List<Task>
    fun getTaskById(id: Long): Task?
    fun insertTask(task: Task): Long
    fun updateTask(task: Task): Boolean
    fun deleteTask(task: Task): Boolean
    fun getPendingTasks(): List<Task>
    fun getCompletedTasks(): List<Task>
}