package com.dndstats.android.ui.characterform

import com.dndstats.gateways.InMemoryCharacterStore
import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.EditCharacter
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterFormViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var store: InMemoryCharacterStore
    private lateinit var createCharacter: CreateCharacter
    private lateinit var editCharacter: EditCharacter

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        store = InMemoryCharacterStore()
        createCharacter = CreateCharacter(store)
        editCharacter = EditCharacter(store)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel(editId: String? = null) =
        CharacterFormViewModel(createCharacter, editCharacter, editId)

    private fun CharacterFormViewModel.fillValidForm(
        name: String = "Bilbo",
        species: String = "Hobbit",
        characterClass: String = "Rogue",
        level: String = "4",
    ) {
        onNameChange(name)
        onSpeciesChange(species)
        onClassChange(characterClass)
        onLevelChange(level)
        onStrengthChange("10")
        onDexterityChange("16")
        onConstitutionChange("12")
        onIntelligenceChange("14")
        onWisdomChange("13")
        onCharismaChange("11")
    }

    @Test
    fun `submit with valid input creates character and sets submittedId`() = runTest {
        val viewModel = buildViewModel()
        viewModel.fillValidForm()
        viewModel.submit()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.submittedId)
        assertTrue(state.validationErrors.isEmpty())
    }

    @Test
    fun `submit with blank name produces validation error`() = runTest {
        val viewModel = buildViewModel()
        viewModel.fillValidForm(name = "")
        viewModel.submit()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.submittedId)
        assertTrue(state.validationErrors.any { it.contains("Name") })
    }

    @Test
    fun `submit with invalid class produces validation error`() = runTest {
        val viewModel = buildViewModel()
        viewModel.fillValidForm(characterClass = "Jedi")
        viewModel.submit()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.submittedId)
        assertTrue(state.validationErrors.any { it.contains("Class") })
    }

    @Test
    fun `submit with out-of-range level produces validation error`() = runTest {
        val viewModel = buildViewModel()
        viewModel.fillValidForm(level = "21")
        viewModel.submit()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNull(state.submittedId)
        assertTrue(state.validationErrors.any { it.contains("Level") })
    }

    @Test
    fun `submit in edit mode updates existing character`() = runTest {
        // First create a character
        val created = createCharacter.execute(
            CreateCharacter.Input(
                name = "Sauron",
                species = "Maia",
                characterClass = "Warlock",
                level = 20,
                abilityScores = CreateCharacter.Input.AbilityScoreInput(20, 14, 20, 20, 16, 20),
            )
        )
        val id = created.id.toString()

        val viewModel = buildViewModel(editId = id)
        viewModel.fillValidForm(name = "Reformed Sauron", characterClass = "Warlock", level = "19")
        viewModel.submit()
        advanceUntilIdle()

        // submittedId should be the same character id
        assertEquals(id, viewModel.uiState.value.submittedId)
        assertTrue(viewModel.uiState.value.validationErrors.isEmpty())
    }

    @Test
    fun `field changes update state correctly`() {
        val viewModel = buildViewModel()
        viewModel.onNameChange("Gimli")
        viewModel.onSpeciesChange("Dwarf")
        viewModel.onClassChange("Fighter")
        viewModel.onLevelChange("7")
        viewModel.onStrengthChange("18")
        viewModel.onArmorClassOverrideChange("15")

        val state = viewModel.uiState.value
        assertEquals("Gimli", state.name)
        assertEquals("Dwarf", state.species)
        assertEquals("Fighter", state.characterClass)
        assertEquals("7", state.level)
        assertEquals("18", state.strength)
        assertEquals("15", state.armorClassOverride)
    }
}
