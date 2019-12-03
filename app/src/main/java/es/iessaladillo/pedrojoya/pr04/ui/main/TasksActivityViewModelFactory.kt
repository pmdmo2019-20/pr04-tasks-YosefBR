package es.iessaladillo.pedrojoya.pr04.ui.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import es.iessaladillo.pedrojoya.pr04.data.Repository

// TODO: Crea una clase TasksActivityViewModelFactory, que implemente ViewModelProvider.Factory
//  para construir un objeto TasksActivityViewModel

class TasksActivityViewModelFactory(private val repository: Repository,
                                    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TasksActivityViewModel(repository, application) as T
    }

}