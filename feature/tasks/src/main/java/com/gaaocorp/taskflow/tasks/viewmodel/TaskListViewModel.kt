package com.gaaocorp.taskflow.tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaaocorp.taskflow.domain.model.Task
import com.gaaocorp.taskflow.domain.usecase.GetTasksUseCase
import com.gaaocorp.taskflow.domain.usecase.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {

    val uiState: StateFlow<TaskListUiState> = getTasksUseCase()
        .map { tasks -> TaskListUiState(tasks = tasks, isLoading = false) }
        .catch { e -> emit(TaskListUiState(isLoading = false, error = e.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TaskListUiState()
        )

    fun toggleTaskComplete(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(
                task.copy(
                    isCompleted = !task.isCompleted,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
