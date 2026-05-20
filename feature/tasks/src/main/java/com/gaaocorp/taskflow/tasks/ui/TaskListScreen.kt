package com.gaaocorp.taskflow.tasks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gaaocorp.taskflow.domain.model.Priority
import com.gaaocorp.taskflow.domain.model.Task
import com.gaaocorp.taskflow.tasks.viewmodel.TaskListViewModel
import com.gaaocorp.taskflow.ui.components.TaskFlowCard
import com.gaaocorp.taskflow.ui.components.TaskFlowTopAppBar
import com.gaaocorp.taskflow.ui.theme.PriorityHigh
import com.gaaocorp.taskflow.ui.theme.PriorityLow
import com.gaaocorp.taskflow.ui.theme.PriorityMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TaskFlowTopAppBar(
                title = "Mis Tareas",
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Nueva tarea")
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.tasks.isEmpty() -> {
                EmptyTasksState(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    onCreateTask = onNavigateToCreate
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onTaskClick = { onNavigateToDetail(task.id) },
                            onToggleComplete = { viewModel.toggleTaskComplete(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onToggleComplete: () -> Unit
) {
    TaskFlowCard(onClick = onTaskClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
            PriorityChip(priority = task.priority)
        }
    }
}

@Composable
private fun PriorityChip(priority: Priority) {
    val (label, color) = when (priority) {
        Priority.HIGH -> "Alta" to PriorityHigh
        Priority.MEDIUM -> "Media" to PriorityMedium
        Priority.LOW -> "Baja" to PriorityLow
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

@Composable
private fun EmptyTasksState(modifier: Modifier, onCreateTask: () -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("No tienes tareas aún", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Crea tu primera tarea para comenzar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCreateTask) {
            Text("Crear tarea")
        }
    }
}
