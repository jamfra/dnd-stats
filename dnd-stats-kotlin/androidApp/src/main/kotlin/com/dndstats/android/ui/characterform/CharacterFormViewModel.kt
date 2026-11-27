package com.dndstats.android.ui.characterform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.EditCharacter
import com.dndstats.usecases.ValidationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

data class CharacterFormUiState(
    val name: String = "",
    val species: String = "",
    val characterClass: String = "",
    val level: String = "1",
    val strength: String = "15",
    val dexterity: String = "13",
    val constitution: String = "14",
    val intelligence: String = "10",
    val wisdom: String = "12",
    val charisma: String = "8",
    val armorClassOverride: String = "",
    val validationErrors: List<String> = emptyList(),
    val isSubmitting: Boolean = false,
    val submittedId: String? = null,  // non-null signals navigation after success
)

class CharacterFormViewModel(
    private val createCharacter: CreateCharacter,
    private val editCharacter: EditCharacter,
    private val editId: String?,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterFormUiState())
    val uiState: StateFlow<CharacterFormUiState> = _uiState.asStateFlow()

    // ── Field updates ────────────────────────────────────────────────────────

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value) }
    fun onSpeciesChange(value: String) = _uiState.update { it.copy(species = value) }
    fun onClassChange(value: String) = _uiState.update { it.copy(characterClass = value) }
    fun onLevelChange(value: String) = _uiState.update { it.copy(level = value) }
    fun onStrengthChange(value: String) = _uiState.update { it.copy(strength = value) }
    fun onDexterityChange(value: String) = _uiState.update { it.copy(dexterity = value) }
    fun onConstitutionChange(value: String) = _uiState.update { it.copy(constitution = value) }
    fun onIntelligenceChange(value: String) = _uiState.update { it.copy(intelligence = value) }
    fun onWisdomChange(value: String) = _uiState.update { it.copy(wisdom = value) }
    fun onCharismaChange(value: String) = _uiState.update { it.copy(charisma = value) }
    fun onArmorClassOverrideChange(value: String) = _uiState.update { it.copy(armorClassOverride = value) }

    // ── Pre-populate form for edit ────────────────────────────────────────────

    fun populateFrom(
        name: String,
        species: String,
        characterClass: String,
        level: Int,
        strength: Int,
        dexterity: Int,
        constitution: Int,
        intelligence: Int,
        wisdom: Int,
        charisma: Int,
        armorClassOverride: Int?,
    ) {
        _uiState.update {
            it.copy(
                name = name,
                species = species,
                characterClass = characterClass,
                level = level.toString(),
                strength = strength.toString(),
                dexterity = dexterity.toString(),
                constitution = constitution.toString(),
                intelligence = intelligence.toString(),
                wisdom = wisdom.toString(),
                charisma = charisma.toString(),
                armorClassOverride = armorClassOverride?.toString() ?: "",
            )
        }
    }

    // ── Submit ───────────────────────────────────────────────────────────────

    fun submit() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, validationErrors = emptyList()) }
            runCatching {
                val state = _uiState.value
                val abilityScores = CreateCharacter.Input.AbilityScoreInput(
                    strength = state.strength.toIntOrNull() ?: 0,
                    dexterity = state.dexterity.toIntOrNull() ?: 0,
                    constitution = state.constitution.toIntOrNull() ?: 0,
                    intelligence = state.intelligence.toIntOrNull() ?: 0,
                    wisdom = state.wisdom.toIntOrNull() ?: 0,
                    charisma = state.charisma.toIntOrNull() ?: 0,
                )
                val armorOverride = state.armorClassOverride.toIntOrNull()

                if (editId == null) {
                    // Create
                    val output = createCharacter.execute(
                        CreateCharacter.Input(
                            name = state.name,
                            species = state.species,
                            characterClass = state.characterClass,
                            level = state.level.toIntOrNull() ?: 0,
                            abilityScores = abilityScores,
                        )
                    )
                    output.id.toString()
                } else {
                    // Edit
                    editCharacter.execute(
                        EditCharacter.Input(
                            id = Uuid.parse(editId),
                            name = state.name,
                            species = state.species,
                            characterClass = state.characterClass,
                            level = state.level.toIntOrNull() ?: 0,
                            abilityScores = EditCharacter.Input.AbilityScoreInput(
                                strength = abilityScores.strength,
                                dexterity = abilityScores.dexterity,
                                constitution = abilityScores.constitution,
                                intelligence = abilityScores.intelligence,
                                wisdom = abilityScores.wisdom,
                                charisma = abilityScores.charisma,
                            ),
                            armorClassOverride = armorOverride,
                        )
                    )
                    editId
                }
            }.fold(
                onSuccess = { resultId ->
                    _uiState.update { it.copy(isSubmitting = false, submittedId = resultId) }
                },
                onFailure = { e ->
                    val errors = when (e) {
                        is ValidationException -> e.errors
                        else -> listOf(e.message ?: "An unexpected error occurred")
                    }
                    _uiState.update { it.copy(isSubmitting = false, validationErrors = errors) }
                },
            )
        }
    }

    class Factory(
        private val createCharacter: CreateCharacter,
        private val editCharacter: EditCharacter,
        private val editId: String?,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CharacterFormViewModel(createCharacter, editCharacter, editId) as T
    }
}
