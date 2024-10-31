package ma.ensa.projet

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import android.widget.TabHost
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import ma.ensa.projet.adapter.TaskAdapter
import ma.ensa.projet.beans.Task
import ma.ensa.projet.databinding.ActivityMainBinding
import ma.ensa.projet.databinding.DialogAddTaskBinding
import ma.ensa.projet.service.TaskService

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskService: TaskService
    private lateinit var menu: Menu
    private lateinit var tabHost: TabHost

    private lateinit var allTasksAdapter: TaskAdapter
    private lateinit var pendingTasksAdapter: TaskAdapter
    private lateinit var completedTasksAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Service
        taskService = TaskService()

        setupTabs()
        setupRecyclerViews()

        binding.fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun setupTabs() {
        tabHost = findViewById(android.R.id.tabhost)
        tabHost.setup()

        // Onglet Toutes les tâches
        var spec = tabHost.newTabSpec("all")
        spec.setIndicator("Toutes")
        spec.setContent(R.id.tab_all_tasks)
        tabHost.addTab(spec)

        // Onglet En cours
        spec = tabHost.newTabSpec("pending")
        spec.setIndicator("En cours")
        spec.setContent(R.id.tab_pending)
        tabHost.addTab(spec)

        // Onglet Terminées
        spec = tabHost.newTabSpec("completed")
        spec.setIndicator("Terminées")
        spec.setContent(R.id.tab_completed)
        tabHost.addTab(spec)

        // Gestion du changement d'onglet
        tabHost.setOnTabChangedListener { tabId ->
            // Réinitialiser la recherche lors du changement d'onglet
            menu.findItem(R.id.action_search)?.let { searchItem ->
                val searchView = searchItem.actionView as? SearchView
                searchView?.setQuery("", false)
                searchView?.isIconified = true
            }

            // Mettre à jour la vue en fonction de l'onglet sélectionné
            when (tabId) {
                "all" -> allTasksAdapter.updateTasks()
                "pending" -> pendingTasksAdapter.updateTasks()
                "completed" -> completedTasksAdapter.updateTasks()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText ?: ""
                when (tabHost.currentTabTag) {
                    "all" -> allTasksAdapter.filter(query)
                    "pending" -> pendingTasksAdapter.filter(query)
                    "completed" -> completedTasksAdapter.filter(query)
                }
                return true
            }
        })

        return true
    }

    private fun setupRecyclerViews() {
        allTasksAdapter = TaskAdapter(
            taskService = taskService,
            onTaskClicked = { task -> showTaskDetailsDialog(task) }
        )

        pendingTasksAdapter = TaskAdapter(
            taskService = taskService,
            onTaskClicked = { task -> showTaskDetailsDialog(task) }
        )

        completedTasksAdapter = TaskAdapter(
            taskService = taskService,
            onTaskClicked = { task -> showTaskDetailsDialog(task) }
        )

        binding.recyclerViewAllTasks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = allTasksAdapter
        }

        binding.recyclerViewPending.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = pendingTasksAdapter
        }

        binding.recyclerViewCompleted.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = completedTasksAdapter
        }
    }

    private fun showAddTaskDialog() {
        val dialogBinding = DialogAddTaskBinding.inflate(layoutInflater)
        AlertDialog.Builder(this)
            .setTitle("Nouvelle tâche")
            .setView(dialogBinding.root)
            .setPositiveButton("Ajouter") { _, _ ->
                val title = dialogBinding.editTextTitle.text.toString()
                val description = dialogBinding.editTextDescription.text.toString()

                if (title.isNotEmpty()) {
                    taskService.createTask(title, description)
                    updateRecyclerViews()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun showTaskDetailsDialog(task: Task) {
        val dialogBinding = DialogAddTaskBinding.inflate(layoutInflater)
        dialogBinding.editTextTitle.setText(task.title)
        dialogBinding.editTextDescription.setText(task.description)

        AlertDialog.Builder(this)
            .setTitle("Détails de la tâche")
            .setView(dialogBinding.root)
            .setPositiveButton("Modifier") { _, _ ->
                val updatedTitle = dialogBinding.editTextTitle.text.toString()
                val updatedDescription = dialogBinding.editTextDescription.text.toString()

                if (updatedTitle.isNotEmpty()) {
                    task.title = updatedTitle
                    task.description = updatedDescription
                    taskService.updateTask(task)
                    updateRecyclerViews()
                }
            }
            .setNeutralButton("Supprimer") { _, _ ->
                taskService.deleteTask(task)
                updateRecyclerViews()
            }
            .setNegativeButton("Fermer", null)
            .show()
    }

    fun updateRecyclerViews() {
        allTasksAdapter.updateTasks()
        pendingTasksAdapter.updateTasks()
        completedTasksAdapter.updateTasks()
    }
}