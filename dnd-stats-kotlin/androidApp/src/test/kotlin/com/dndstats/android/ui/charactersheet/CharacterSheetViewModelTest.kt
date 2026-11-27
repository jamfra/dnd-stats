package com.dndstats.android.ui.charactersheet

import com.dndstats.gateways.InMemoryCharacterStore
import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.GetCharacterSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterSheetViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var store: InMemoryCharacterStore
    private lateinit var getCharacterSheet: GetCharacterSheet
    private lateinit var createCharacter: CreateCharacter

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        store = InMemoryCharacterStore()
        getCharacterSheet = GetCharacterSheet(store)
        createCharacter = CreateCharacter(store)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun defaultScores() = CreateCharacter.Input.AbilityScoreInput(15, 13, 14, 10, 12, 8)

    @Test
    fun `loadSheet populates Success state for existing character`() = runTest {
        val output = createCharacter.execute(
            CreateCharacter.Input("Thorin", "Dwarf", "Fighter", 3, defaultScores())
        )
        val viewModel = CharacterSheetViewModel(getCharacterSheet, output.id.toString())
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<CharacterSheetUiState.Success>(state)
        assertEquals("Thorin", state.sheet.name)
        assertEquals("Dwarf", state.sheet.species)
        assertEquals("Fighter", state.sheet.characterClass)
        assertEquals(3, state.sheet.level)
    }

    @Test
    fun `loadSheet produces Error state for unknown character id`() = runTest {
        val viewModel = CharacterSheetViewModel(getCharacterSheet, "00000000-0000-0000-0000-000000000000")
        advanceUntilIdle()

        assertIs<CharacterSheetUiState.Error>(viewModel.uiState.value)
    }
}
