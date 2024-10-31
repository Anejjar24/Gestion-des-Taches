package ma.ensa.projet.service

import ma.ensa.projet.beans.Task
import ma.ensa.projet.dao.TaskDaoImpl

class TaskService {
    private val taskDao = TaskDaoImpl()

    fun getAllTasks(): List<Task> = taskDao.getAllTasks()

    fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    fun createTask(title: String, description: String): Task {
        val task = Task(
            id = System.currentTimeMillis(),
            title = title,
            description = description
        )
        taskDao.insertTask(task)
        return task
    }

    fun updateTask(task: Task): Boolean = taskDao.updateTask(task)

    fun deleteTask(task: Task): Boolean = taskDao.deleteTask(task)

    fun toggleTaskCompletion(task: Task): Boolean {
        task.isCompleted = !task.isCompleted
        return taskDao.updateTask(task)
    }

    fun getPendingTasks(): List<Task> = taskDao.getPendingTasks()

    fun getCompletedTasks(): List<Task> = taskDao.getCompletedTasks()

    fun searchTasks(query: String): List<Task> {
        return if (query.isEmpty()) {
            getAllTasks()
        } else {
            getAllTasks().filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
    }
}
