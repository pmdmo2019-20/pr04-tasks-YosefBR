package es.iessaladillo.pedrojoya.pr04.ui.main

import android.app.Application
import android.content.Intent
import android.provider.Settings.Global.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import es.iessaladillo.pedrojoya.pr04.R
import es.iessaladillo.pedrojoya.pr04.base.Event
import es.iessaladillo.pedrojoya.pr04.data.Repository
import es.iessaladillo.pedrojoya.pr04.data.entity.Task

class TasksActivityViewModel(private val repository: Repository,
                             private val application: Application) : ViewModel() {

    private val taskIdList: MutableList<Long> = mutableListOf()

    // Estado de la interfaz
    private val _tasks: MutableLiveData<List<Task>> = MutableLiveData()
    val tasks: LiveData<List<Task>>
        get() = _tasks

    private val _currentFilter: MutableLiveData<TasksActivityFilter> =
        MutableLiveData(TasksActivityFilter.ALL)

    private val _currentFilterMenuItemId: MutableLiveData<Int> =
        MutableLiveData(R.id.mnuFilterAll)
    val currentFilterMenuItemId: LiveData<Int>
        get() = _currentFilterMenuItemId

    private val _activityTitle: MutableLiveData<String> =
        MutableLiveData(application.getString(R.string.tasks_title_all))
    val activityTitle: LiveData<String>
        get() = _activityTitle

    private val _lblEmptyViewText: MutableLiveData<String> =
        MutableLiveData(application.getString(R.string.tasks_no_tasks_yet))
    val lblEmptyViewText: LiveData<String>
        get() = _lblEmptyViewText

    // Eventos de comunicación con la actividad

    private val _onStartActivity: MutableLiveData<Event<Intent>> = MutableLiveData()
    val onStartActivity: LiveData<Event<Intent>>
        get() = _onStartActivity

    private val _onShowMessage: MutableLiveData<Event<String>> = MutableLiveData()
    val onShowMessage: LiveData<Event<String>>
        get() = _onShowMessage

    private val _onShowTaskDeleted: MutableLiveData<Event<Task>> = MutableLiveData()
    val onShowTaskDeleted: LiveData<Event<Task>>
        get() = _onShowTaskDeleted

    // ACTION METHODS

    // Hace que se muestre en el RecyclerView todas las tareas.
    fun filterAll() {
        _currentFilterMenuItemId.value = R.id.mnuFilterAll
        _activityTitle.value = application.getString(R.string.tasks_title_all)
        queryTasks(TasksActivityFilter.ALL)
    }

    // Hace que se muestre en el RecyclerView sólo las tareas completadas.
    fun filterCompleted() {
        _currentFilterMenuItemId.value = R.id.mnuFilterCompleted
        _activityTitle.value = application.getString(R.string.tasks_title_completed)
        queryTasks(TasksActivityFilter.COMPLETED)
    }

    // Hace que se muestre en el RecyclerView sólo las tareas pendientes.
    fun filterPending() {
        _currentFilterMenuItemId.value = R.id.mnuFilterPending
        _activityTitle.value = application.getString(R.string.tasks_title_pending)
        queryTasks(TasksActivityFilter.PENDING)
    }

    fun emptyList(): Boolean {
        return taskIdList.isEmpty()
    }

    // Agrega una nueva tarea con dicho concepto. Si la se estaba mostrando
    // la lista de solo las tareas completadas, una vez agregada se debe
    // mostrar en el RecyclerView la lista con todas las tareas, no sólo
    // las completadas.
    fun addTask(concept: String) {
        repository.addTask(concept)
        filterAll()
    }

    // Borra la tarea
    fun deleteTask(taskId: Long) {
        repository.deleteTask(taskId)
        when {
            _activityTitle.value == "Tasks" -> queryTasks(TasksActivityFilter.ALL)
            _activityTitle.value == "Tasks (completed)" -> queryTasks(TasksActivityFilter.COMPLETED)
            else -> queryTasks(TasksActivityFilter.PENDING)
        }
    }

    // Agrega la tarea
    fun insertTask(task: Task) {
        repository.insertTask(task)
        when {
            _activityTitle.value == "Tasks" -> queryTasks(TasksActivityFilter.ALL)
            _activityTitle.value == "Tasks (completed)" -> queryTasks(TasksActivityFilter.COMPLETED)
            else -> queryTasks(TasksActivityFilter.PENDING)
        }
    }

    // Borra todas las tareas mostradas actualmente en el RecyclerView.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que borrar.
    fun deleteTasks() {
        if (taskIdList.isEmpty()) {
        }
        else {
            repository.deleteTasks(taskIdList)
            when {
                _activityTitle.value == "Tasks" -> queryTasks(TasksActivityFilter.ALL)
                _activityTitle.value == "Tasks (completed)" -> queryTasks(TasksActivityFilter.COMPLETED)
                else -> queryTasks(TasksActivityFilter.PENDING)
            }
        }
    }

    // Marca como completadas todas las tareas mostradas actualmente en el RecyclerView,
    // incluso si ya estaban completadas.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que marcar como completadas.
    fun markTasksAsCompleted() {
        repository.markTasksAsCompleted(taskIdList)
        when {
            _activityTitle.value == "Tasks" -> queryTasks(TasksActivityFilter.ALL)
            _activityTitle.value == "Tasks (completed)" -> queryTasks(TasksActivityFilter.COMPLETED)
            else -> queryTasks(TasksActivityFilter.PENDING)
        }
    }

    // Marca como pendientes todas las tareas mostradas actualmente en el RecyclerView,
    // incluso si ya estaban pendientes.
    // Si no se estaba mostrando ninguna tarea, se muestra un mensaje
    // informativo en un SnackBar de que no hay tareas que marcar como pendientes.
    fun markTasksAsPending() {
        repository.markTasksAsPending(taskIdList)
        when {
            _activityTitle.value == "Tasks" -> queryTasks(TasksActivityFilter.ALL)
            _activityTitle.value == "Tasks (completed)" -> queryTasks(TasksActivityFilter.COMPLETED)
            else -> queryTasks(TasksActivityFilter.PENDING)
        }
    }

    // Hace que se envíe un Intent con la lista de tareas mostradas actualmente
    // en el RecyclerView.
    // Si no se estaba mostrando ninguna tarea, se muestra un Snackbar indicando
    // que no hay tareas que compartir.
    fun shareTasks(): Intent {
        //Creamos una lista de strings para guardar las tareas a enviar
        val listOfTasks = arrayListOf<String>()
        // taskSave ira guardando todas las tareas visibles
        var taskSave = ""
        // tasksToShare sera un string con todas las tareas de listOfTasks con saltos de linea
        var taskToShare = ""
        tasks.value?.forEach {
            //Nos guarda el concepto de la tarea y su estado
            taskSave += it.concept
            taskSave += if (it.completed) {
                " Completado"
            } else {
                " Pendiente"
            }
            listOfTasks.add(taskSave)
            //Reiniciamos el string para poder guardar la siguiente tarea
            taskSave = ""
        }
        //Recorremos la lista de Strings para almacenarla en la variable que las enviará con formato
        listOfTasks.forEach {
            taskToShare += it + "\n"
        }
        //Preparamos y retornamos el intent que será enviado al llamar a StartActivity()
        val intent = Intent(Intent.ACTION_SEND).apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, "Tareas")
            putExtra(Intent.EXTRA_TEXT, taskToShare)
            type = "text/plain"
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        return intent
    }

    private fun queryListData(newList: List<Task>) {
        _tasks.value = newList.sortedByDescending { it.id }
        taskIdList.clear()
        tasks.value?.forEach {
            taskIdList.add(it.id)
        }
    }

    // Actualiza el estado de completitud de la tarea recibida, atendiendo al
    // valor de isCompleted. Si es true la tarea es marcada como completada y
    // en caso contrario es marcada como pendiente.
    fun updateTaskCompletedState(task: Task, isCompleted: Boolean) {
        if (!isCompleted) {
            repository.markTaskAsCompleted(task.id)
        }
        else {
            repository.markTaskAsPending(task.id)
        }
        when {
            _activityTitle.value == "Tasks" -> queryTasks(TasksActivityFilter.ALL)
            _activityTitle.value == "Tasks (completed)" -> queryTasks(TasksActivityFilter.COMPLETED)
            else -> queryTasks(TasksActivityFilter.PENDING)
        }
    }

    // Retorna si el concepto recibido es válido (no es una cadena vacía o en blanco)
    fun isValidConcept(concept: String): Boolean {
        return concept != ""
    }

    // Pide las tareas al repositorio, atendiendo al filtro recibido
    private fun queryTasks(filter: TasksActivityFilter) {
        when (filter) {
            TasksActivityFilter.ALL ->
                queryListData(repository.queryAllTasks())
            TasksActivityFilter.COMPLETED ->
                queryListData(repository.queryCompletedTasks())
            TasksActivityFilter.PENDING ->
                queryListData(repository.queryPendingTasks())
        }
    }

}

