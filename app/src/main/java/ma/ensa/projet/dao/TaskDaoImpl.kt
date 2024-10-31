package ma.ensa.projet.dao

import ma.ensa.projet.beans.Task

class TaskDaoImpl {
    private val tasks = mutableListOf<Task>()

    fun getAllTasks(): List<Task> = tasks.toList()

    fun getTaskById(id: Long): Task? = tasks.find { it.id == id }

    fun insertTask(task: Task): Long {
        tasks.add(task)
        return task.id
    }

    fun updateTask(task: Task): Boolean {
        val index = tasks.indexOfFirst { it.id == task.id }
        return if (index != -1) {
            tasks[index] = task
            true
        } else false
    }

    fun deleteTask(task: Task): Boolean = tasks.remove(task)

    fun getPendingTasks(): List<Task> = tasks.filter { !it.isCompleted }

    fun getCompletedTasks(): List<Task> = tasks.filter { it.isCompleted }
}
