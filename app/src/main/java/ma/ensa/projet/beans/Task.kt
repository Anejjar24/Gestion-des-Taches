package ma.ensa.projet.beans

data class Task(
    val id: Long,
    var title: String,
    var description: String,
    var isCompleted: Boolean = false,
    var dueDate: Long? = null
)