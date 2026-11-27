package com.dndstats.android.ui.characterform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dndstats.usecases.CreateCharacter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterFormScreen(
    viewModel: CharacterFormViewModel,
    onSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate when submission succeeds
    LaunchedEffect(uiState.submittedId) {
        uiState.submittedId?.let { onSuccess(it) }
    }

    val isEdit = viewModel.uiState.value.let { false } // determined by factory; title adapts via editId
    val title = "Character"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                // Validation errors
                if (uiState.validationErrors.isNotEmpty()) {
                    uiState.validationErrors.forEach { error ->
                        Text(
                            text = "• $error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Name
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text("Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Species
                OutlinedTextField(
                    value = uiState.species,
                    onValueChange = viewModel::onSpeciesChange,
                    label = { Text("Species") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Class dropdown
                ClassDropdown(
                    selectedClass = uiState.characterClass,
                    onClassSelected = viewModel::onClassChange,
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Level
                OutlinedTextField(
                    value = uiState.level,
                    onValueChange = viewModel::onLevelChange,
                    label = { Text("Level (1–20) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Ability Scores (1–30)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                AbilityScoreField("Strength", uiState.strength, viewModel::onStrengthChange)
                AbilityScoreField("Dexterity", uiState.dexterity, viewModel::onDexterityChange)
                AbilityScoreField("Constitution", uiState.constitution, viewModel::onConstitutionChange)
                AbilityScoreField("Intelligence", uiState.intelligence, viewModel::onIntelligenceChange)
                AbilityScoreField("Wisdom", uiState.wisdom, viewModel::onWisdomChange)
                AbilityScoreField("Charisma", uiState.charisma, viewModel::onCharismaChange)

                Spacer(modifier = Modifier.height(16.dp))

                // Optional armor class override
                OutlinedTextField(
                    value = uiState.armorClassOverride,
                    onValueChange = viewModel::onArmorClassOverrideChange,
                    label = { Text("Armor Class Override (optional, 1–30)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = viewModel::submit,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSubmitting,
                ) {
                    Text("Save")
                }
            }

            if (uiState.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassDropdown(
    selectedClass: String,
    onClassSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedClass,
            onValueChange = {},
            readOnly = true,
            label = { Text("Class *") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            CreateCharacter.AVAILABLE_CLASSES.forEach { className ->
                DropdownMenuItem(
                    text = { Text(className) },
                    onClick = {
                        onClassSelected(className)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun AbilityScoreField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
}
