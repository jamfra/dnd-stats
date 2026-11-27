package com.dndstats.android.ui.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dndstats.usecases.DeleteCharacter
import com.dndstats.usecases.ListCharacters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

data class CharacterListUiState(
    val characters: List<ListCharacters.CharacterSummary> = emptyList(),
    val isLoading: Boolean = false,
    val pendingDeleteId: String? = null,
)

class CharacterListViewModel(
    private val listCharacters: ListCharacters,
    private val deleteCharacter: DeleteCharacter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterListUiState(isLoading = true))
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val output = listCharacters.execute()
            _uiState.update { it.copy(characters = output.characters, isLoading = false) }
        }
    }

    fun requestDelete(id: String) {
        _uiState.update { it.copy(pendingDeleteId = id) }
    }

    fun cancelDelete() {
        _uiState.update { it.copy(pendingDeleteId = null) }
    }

    fun confirmDelete() {
        val id = _uiState.value.pendingDeleteId ?: return
        viewModelScope.launch {
            runCatching { deleteCharacter.execute(DeleteCharacter.Input(Uuid.parse(id))) }
            _uiState.update { it.copy(pendingDeleteId = null) }
            loadCharacters()
        }
    }

    class Factory(
        private val listCharacters: ListCharacters,
        private val deleteCharacter: DeleteCharacter,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CharacterListViewModel(listCharacters, deleteCharacter) as T
    }
}
