package es.iessaladillo.pedrojoya.pr04.data

import android.os.Build
import androidx.annotation.RequiresApi
import es.iessaladillo.pedrojoya.pr04.data.entity.Task
import java.time.LocalDateTime

object LocalRepository : Repository {

    private val tasks: MutableList<Task> = mutableListOf()
    private var id: Long = 1

    override fun queryAllTasks(): List<Task> {
        return ArrayList(tasks)
    }

    override fun queryCompletedTasks(): List<Task> {
        return ArrayList(tasks).filter {
            it.completed
        }
    }

    override fun queryPendingTasks(): List<Task> {
        return ArrayList(tasks).filter {
            !it.completed
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun addTask(concept: String) {
        val current = LocalDateTime.now()
        val newTask = Task(id, concept, "Created at $current", false, current.toString())
        tasks.add(newTask)
        id = (tasks.size + 1).toLong()
    }

    override fun insertTask(task: Task) {
        tasks.add(task)
        id = (tasks.size + 1).toLong()
    }

    override fun deleteTask(taskId: Long) {
        tasks.remove(tasks.filter { x -> x.id==taskId}.last())
    }

    override fun deleteTasks(taskIdList: List<Long>) {
        for (taskId in taskIdList) {
            deleteTask(taskId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markTaskAsCompleted(taskId: Long) {
        val current = LocalDateTime.now()
        tasks.forEach {
            if (it.id == taskId) {
                it.completed = true
                it.completedAt = "Completed at: $current"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun markTasksAsCompleted(taskIdList: List<Long>) {
        val current = LocalDateTime.now()
        tasks.forEach {
            if (taskIdList.contains(it.id)) {
                it.completed = true
                it.completedAt = "Completed at: $current"
            }
        }
    }

    override fun markTaskAsPending(taskId: Long) {
        tasks.forEach {
            if (it.id == taskId) {
                it.completed = false
            }
        }
    }

    override fun markTasksAsPending(taskIdList: List<Long>) {
        tasks.forEach {
            if (taskIdList.contains(it.id)) {
                it.completed = false
            }
        }
    }
}