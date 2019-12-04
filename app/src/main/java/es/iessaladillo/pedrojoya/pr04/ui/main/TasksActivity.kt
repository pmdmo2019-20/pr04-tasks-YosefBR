package es.iessaladillo.pedrojoya.pr04.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.data.LocalRepository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import es.iessaladillo.pedrojoya.pr04.utils.invisibleUnless
import es.iessaladillo.pedrojoya.pr04.utils.setOnSwipeListener
import kotlinx.android.synthetic.main.tasks_activity.*
import kotlinx.android.synthetic.main.tasks_activity_item.*


class TasksActivity : AppCompatActivity() {

    private val viewModel: TasksActivityViewModel by viewModels {
        TasksActivityViewModelFactory(LocalRepository, application)
    }
    private var mnuFilter: MenuItem? = null
    private val listAdapter: TasksActivityAdapter = TasksActivityAdapter().also {
        it.onItemClickListener = { position ->
            checkOrUncheck(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_activity)
        setupViews()
        observeTasks()
        observeMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        mnuFilter = menu.findItem(R.id.mnuFilter)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setupViews() {
        setupRecyclerView()
        imgAddTask.setOnClickListener {
            addNewTask()
        }
    }

    private fun checkOrUncheck(position: Int) {
        val task = listAdapter.getItem(position)
        viewModel.updateTaskCompletedState(task, task.completed)
    }

    private fun addNewTask() {
        val concept = txtConcept.text.toString()
        if (viewModel.isValidConcept(concept)){
            viewModel.addTask(concept)
        }
        txtConcept.text.clear()
    }

    private fun deleteTask(task: Task) {
        viewModel.deleteTask(task.id)
        Snackbar.make(lstTasks, getString(R.string.tasks_task_deleted, task.concept), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.undo)) { viewModel.insertTask(task) }
            .show()
    }

    private fun setupRecyclerView() {
        lstTasks.run {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            itemAnimator = DefaultItemAnimator()
            adapter = listAdapter
            setOnSwipeListener { viewHolder, _ -> deleteTask(listAdapter.getItem(viewHolder.adapterPosition))}
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuShare -> shareTasks()
            R.id.mnuDelete -> deleteTasks()
            R.id.mnuComplete -> markTasksAsCompleted()
            R.id.mnuPending -> markTasksAsPending()
            R.id.mnuFilterAll -> viewModel.filterAll()
            R.id.mnuFilterPending -> viewModel.filterPending()
            R.id.mnuFilterCompleted -> viewModel.filterCompleted()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun shareTasks() {
        if (viewModel.emptyList()) {
            Snackbar.make(lstTasks, getString(R.string.tasks_no_tasks_to_share), Snackbar.LENGTH_LONG).show()
        }
        else {
            startActivity(viewModel.shareTasks())
        }
    }

    private fun deleteTasks() {
        if (viewModel.emptyList()) {
            Snackbar.make(lstTasks, getString(R.string.lis_is_empty), Snackbar.LENGTH_LONG).show()
        }
        else {
            viewModel.deleteTasks()
        }
    }

    private fun markTasksAsCompleted() {
        if (viewModel.emptyList()) {
            Snackbar.make(lstTasks, getString(R.string.tasks_no_tasks_to_mark_as_completed), Snackbar.LENGTH_LONG).show()
        }
        else {
            viewModel.markTasksAsCompleted()
        }
    }

    private fun markTasksAsPending() {
        if (viewModel.emptyList()) {
            Snackbar.make(lstTasks, getString(R.string.tasks_no_tasks_to_mark_as_pending), Snackbar.LENGTH_LONG).show()
        }
        else {
            viewModel.markTasksAsPending()
        }
    }

    private fun observeTasks() {
       viewModel.tasks.observe(this) {
           showTasks(it)
       }
    }

    private fun observeMenu() {
        viewModel.activityTitle.observe(this) {
            this.title = it
        }
        viewModel.currentFilterMenuItemId.observe(this) {
            checkMenuItem(it)
        }
    }

    private fun checkMenuItem(@MenuRes menuItemId: Int) {
        lstTasks.post {
            val item = mnuFilter?.subMenu?.findItem(menuItemId)
            item?.let { menuItem ->
                menuItem.isChecked = true
            }
        }
    }

    private fun showTasks(newList: List<Task>) {
        lstTasks.post {
            listAdapter.submitList(newList)
            lblEmptyView.invisibleUnless(newList.isEmpty())
        }
    }
}

