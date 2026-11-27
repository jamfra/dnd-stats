package com.dndstats.android.ui.charactersheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dndstats.usecases.GetCharacterSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSheetScreen(
    viewModel: CharacterSheetViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Character Sheet") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            if (uiState is CharacterSheetUiState.Success) {
                FloatingActionButton(onClick = onNavigateToEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit character")
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState) {
                is CharacterSheetUiState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is CharacterSheetUiState.Error ->
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.error,
                    )

                is CharacterSheetUiState.Success ->
                    CharacterSheetContent(state.sheet)
            }
        }
    }
}

@Composable
private fun CharacterSheetContent(sheet: GetCharacterSheet.SheetDto) {
    val profSign = formatModifier(sheet.proficiencyBonus)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // ── Info panel ──────────────────────────────────────────────────────
        SectionHeader("Character Info")
        InfoRow("Name", sheet.name)
        InfoRow("Species", sheet.species)
        InfoRow("Class", sheet.characterClass)
        InfoRow("Level", sheet.level.toString())

        // ── Vitals ──────────────────────────────────────────────────────────
        SectionHeader("Vitals")
        InfoRow("Proficiency bonus", profSign)
        InfoRow("Hit points", sheet.hitpoints.toString())
        InfoRow("Armor class", sheet.armourClass.toString())

        // ── Ability Scores ──────────────────────────────────────────────────
        SectionHeader("Ability Scores")
        listOf("strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma").forEach { key ->
            val stat = sheet.abilityScores[key] ?: return@forEach
            InfoRow(key.replaceFirstChar { it.uppercase() }, "${stat.score} (${formatModifier(stat.modifier)})")
        }

        // ── Saving Throws ───────────────────────────────────────────────────
        SectionHeader("Saving Throws")
        listOf("strength", "dexterity", "constitution", "intelligence", "wisdom", "charisma").forEach { key ->
            val stat = sheet.abilityScores[key] ?: return@forEach
            val profStr = if (stat.proficient) " ✦" else ""
            InfoRow(key.replaceFirstChar { it.uppercase() }, "${formatModifier(stat.savingThrow)}$profStr")
        }

        // ── Weapons ─────────────────────────────────────────────────────────
        SectionHeader("Weapons")
        if (sheet.weapons.isEmpty()) {
            Text("No weapon equipped", style = MaterialTheme.typography.bodyMedium)
        } else {
            sheet.weapons.forEach { weapon -> Text("• $weapon", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Text(
        text = "$label: $value",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(vertical = 2.dp),
    )
}

private fun formatModifier(value: Int) = if (value >= 0) "+$value" else "$value"
