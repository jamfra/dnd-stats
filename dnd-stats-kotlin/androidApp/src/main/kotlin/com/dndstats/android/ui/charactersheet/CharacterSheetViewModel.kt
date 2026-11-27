package com.dndstats.android.ui.charactersheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dndstats.usecases.GetCharacterSheet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

sealed interface CharacterSheetUiState {
    data object Loading : CharacterSheetUiState
    data class Success(val sheet: GetCharacterSheet.SheetDto) : CharacterSheetUiState
    data class Error(val message: String) : CharacterSheetUiState
}

class CharacterSheetViewModel(
    private val getCharacterSheet: GetCharacterSheet,
    private val characterId: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharacterSheetUiState>(CharacterSheetUiState.Loading)
    val uiState: StateFlow<CharacterSheetUiState> = _uiState.asStateFlow()

    init {
        loadSheet()
    }

    fun loadSheet() {
        viewModelScope.launch {
            _uiState.update { CharacterSheetUiState.Loading }
            runCatching {
                getCharacterSheet.execute(GetCharacterSheet.Input(Uuid.parse(characterId)))
            }.fold(
                onSuccess = { output -> _uiState.update { CharacterSheetUiState.Success(output.sheet) } },
                onFailure = { e -> _uiState.update { CharacterSheetUiState.Error(e.message ?: "Unknown error") } },
            )
        }
    }

    class Factory(
        private val getCharacterSheet: GetCharacterSheet,
        private val characterId: String,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CharacterSheetViewModel(getCharacterSheet, characterId) as T
    }
}
