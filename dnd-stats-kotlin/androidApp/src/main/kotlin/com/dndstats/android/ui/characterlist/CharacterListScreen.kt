package com.dndstats.android.ui.characterlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dndstats.usecases.ListCharacters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    viewModel: CharacterListViewModel,
    onNavigateToSheet: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadCharacters()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Characters") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Create character")
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                uiState.characters.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("No characters yet")
                        TextButton(onClick = onNavigateToCreate) {
                            Text("Create your first character")
                        }
                    }
                }

                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.characters, key = { it.id }) { character ->
                            CharacterRow(
                                character = character,
                                onClick = { onNavigateToSheet(character.id) },
                                onDelete = { viewModel.requestDelete(character.id) },
                            )
                        }
                    }
                }
            }
        }

        // Delete confirmation dialog
        uiState.pendingDeleteId?.let { id ->
            val character = uiState.characters.find { it.id == id }
            AlertDialog(
                onDismissRequest = { viewModel.cancelDelete() },
                title = { Text("Delete character") },
                text = { Text("Are you sure you want to delete ${character?.name ?: "this character"}?") },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmDelete() }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.cancelDelete() }) {
                        Text("Cancel")
                    }
                },
            )
        }
    }
}

@Composable
private fun CharacterRow(
    character: ListCharacters.CharacterSummary,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(text = character.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${character.characterClass} · Level ${character.level}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete ${character.name}")
            }
        }
    }
}
