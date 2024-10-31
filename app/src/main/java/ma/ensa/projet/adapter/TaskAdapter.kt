package ma.ensa.projet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import ma.ensa.projet.MainActivity
import ma.ensa.projet.beans.Task
import ma.ensa.projet.databinding.ItemTaskBinding
import ma.ensa.projet.service.TaskService
class TaskAdapter(
    private val taskService: TaskService,
    private val onTaskClicked: (Task) -> Unit,
    private val filterType: FilterType = FilterType.ALL // Nouveau paramètre
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    enum class FilterType {
        ALL,
        PENDING,
        COMPLETED
    }

    private var tasks: List<Task> = emptyList()
    private var filteredTasks: List<Task> = emptyList()

    init {
        updateTasks()
    }

    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateTasks() {
        // Récupérer les tâches selon le type de filtre
        tasks = when (filterType) {
            FilterType.ALL -> taskService.getAllTasks()
            FilterType.PENDING -> taskService.getPendingTasks()
            FilterType.COMPLETED -> taskService.getCompletedTasks()
        }
        filteredTasks = tasks
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredTasks = if (query.isEmpty()) {
            tasks
        } else {
            tasks.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = filteredTasks[position]
        with(holder.binding) {
            textViewTitle.text = task.title
            textViewDescription.text = task.description
            checkBoxCompleted.isChecked = task.isCompleted

            buttonDelete.setOnClickListener {
                AlertDialog.Builder(holder.itemView.context)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment supprimer cette tâche ?")
                    .setPositiveButton("Oui") { _, _ ->
                        taskService.deleteTask(task)
                        updateTasks()
                    }
                    .setNegativeButton("Non", null)
                    .show()
            }

            checkBoxCompleted.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                taskService.updateTask(task)
                (holder.itemView.context as? MainActivity)?.updateRecyclerViews()
            }

            root.setOnClickListener { onTaskClicked(task) }
        }
    }

    override fun getItemCount() = filteredTasks.size
}