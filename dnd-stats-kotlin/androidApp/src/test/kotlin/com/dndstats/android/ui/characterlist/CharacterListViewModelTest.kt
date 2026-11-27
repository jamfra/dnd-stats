package com.dndstats.android.ui.characterlist

import com.dndstats.gateways.InMemoryCharacterStore
import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.DeleteCharacter
import com.dndstats.usecases.ListCharacters
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var store: InMemoryCharacterStore
    private lateinit var listCharacters: ListCharacters
    private lateinit var deleteCharacter: DeleteCharacter
    private lateinit var createCharacter: CreateCharacter

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        store = InMemoryCharacterStore()
        listCharacters = ListCharacters(store)
        deleteCharacter = DeleteCharacter(store)
        createCharacter = CreateCharacter(store)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun defaultScores() = CreateCharacter.Input.AbilityScoreInput(15, 13, 14, 10, 12, 8)

    private fun createTestCharacter(name: String = "Aragorn") =
        createCharacter.execute(
            CreateCharacter.Input(
                name = name,
                species = "Human",
                characterClass = "Fighter",
                level = 5,
                abilityScores = defaultScores(),
            )
        )

    @Test
    fun `initial state shows empty list after loading`() = runTest {
        val viewModel = CharacterListViewModel(listCharacters, deleteCharacter)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.characters.isEmpty())
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `loadCharacters populates list from store`() = runTest {
        createTestCharacter("Gandalf")
        createTestCharacter("Frodo")

        val viewModel = CharacterListViewModel(listCharacters, deleteCharacter)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.characters.size)
        assertTrue(state.characters.any { it.name == "Gandalf" })
        assertTrue(state.characters.any { it.name == "Frodo" })
    }

    @Test
    fun `requestDelete sets pendingDeleteId`() = runTest {
        val output = createTestCharacter()
        val id = output.id.toString()

        val viewModel = CharacterListViewModel(listCharacters, deleteCharacter)
        advanceUntilIdle()

        viewModel.requestDelete(id)

        assertEquals(id, viewModel.uiState.value.pendingDeleteId)
    }

    @Test
    fun `cancelDelete clears pendingDeleteId`() = runTest {
        val output = createTestCharacter()
        val viewModel = CharacterListViewModel(listCharacters, deleteCharacter)
        advanceUntilIdle()

        viewModel.requestDelete(output.id.toString())
        viewModel.cancelDelete()

        assertNull(viewModel.uiState.value.pendingDeleteId)
    }

    @Test
    fun `confirmDelete removes character and refreshes list`() = runTest {
        val output = createTestCharacter("Legolas")
        val id = output.id.toString()

        val viewModel = CharacterListViewModel(listCharacters, deleteCharacter)
        advanceUntilIdle()

        viewModel.requestDelete(id)
        viewModel.confirmDelete()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.characters.isEmpty())
        assertNull(state.pendingDeleteId)
    }
}
