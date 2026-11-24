package com.dndstats.usecases

import com.dndstats.domain.AbilityScores
import com.dndstats.domain.Character
import com.dndstats.gateways.CharacterStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class EditCharacterTest {

    private val store = mockk<CharacterStore>()
    private val editCharacter = EditCharacter(store)
    private val characterId = UUID.randomUUID()

    private val validInput = EditCharacter.Input(
        id = characterId,
        name = "Aria the Valiant",
        species = "Elf",
        characterClass = "Paladin",
        level = 5,
        abilityScores = EditCharacter.Input.AbilityScoreInput(16, 14, 14, 10, 12, 16),
        armorClassOverride = 18,
    )

    @Test
    fun `calls update on store with the updated character`() {
        val captured = slot<Character>()
        every { store.update(characterId, capture(captured)) } returns true

        editCharacter.execute(validInput)

        verify { store.update(characterId, any()) }
        assertEquals(
            Character(
                name = "Aria the Valiant",
                species = "Elf",
                characterClass = "Paladin",
                level = 5,
                abilityScores = AbilityScores(16, 14, 14, 10, 12, 16),
                armourClassOverride = 18,
            ),
            captured.captured,
        )
    }

    @Test
    fun `throws NoSuchElementException when character does not exist in store`() {
        every { store.update(any(), any()) } returns false

        val ex = assertThrows<NoSuchElementException> {
            editCharacter.execute(validInput)
        }
        assertTrue(ex.message!!.contains(characterId.toString()))
    }

    @Test
    fun `rejects empty name`() {
        val ex = assertThrows<ValidationException> {
            editCharacter.execute(validInput.copy(name = "   "))
        }
        assertTrue(ex.errors.any { it.contains("Name", ignoreCase = true) })
        verify(exactly = 0) { store.update(any(), any()) }
    }

    @Test
    fun `rejects name exceeding 50 characters`() {
        val ex = assertThrows<ValidationException> {
            editCharacter.execute(validInput.copy(name = "a".repeat(51)))
        }
        assertTrue(ex.errors.any { it.contains("Name", ignoreCase = true) })
        verify(exactly = 0) { store.update(any(), any()) }
    }

    @Test
    fun `rejects species exceeding 50 characters`() {
        val ex = assertThrows<ValidationException> {
            editCharacter.execute(validInput.copy(species = "a".repeat(51)))
        }
        assertTrue(ex.errors.any { it.contains("Species", ignoreCase = true) })
        verify(exactly = 0) { store.update(any(), any()) }
    }

    @Test
    fun `rejects invalid class`() {
        val ex = assertThrows<ValidationException> {
            editCharacter.execute(validInput.copy(characterClass = "Jedi"))
        }
        assertTrue(ex.errors.any { it.contains("Class", ignoreCase = true) })
        verify(exactly = 0) { store.update(any(), any()) }
    }

    @Test
    fun `rejects level less than 1`() {
        val ex = assertThrows<ValidationException> {
            editCharacter.execute(validInput.copy(level = 0))
        }
        assertTrue(ex.errors.any { it.contains("Level", ignoreCase = true) })
        verify(exactly = 0) { store.update(any(), any()) }
    }

    @Test
    fun `rejects level greater than 20`() {
        val ex = assertThrows<ValidationException> {
            editCharacter.execute(validInput.copy(level = 21))
        }
        assertTrue(ex.errors.any { it.contains("Level", ignoreCase = true) })
        verify(exactly = 0) { store.update(any(), any()) }
    }

    @Test
    fun `rejects ability score out of range`() {
        val ex = assertThrows<ValidationException> {
            editCharacter.execute(
                validInput.copy(
                    abilityScores = validInput.abilityScores.copy(strength = 0),
                )
            )
        }
        assertTrue(ex.errors.any { it.contains("Strength", ignoreCase = true) })
        verify(exactly = 0) { store.update(any(), any()) }
    }

    @Test
    fun `rejects armor class override less than 1`() {
        val ex = assertThrows<ValidationException> {
            editCharacter.execute(validInput.copy(armorClassOverride = 0))
        }
        assertTrue(ex.errors.any { it.contains("Armor class", ignoreCase = true) || it.contains("Armour class", ignoreCase = true) })
        verify(exactly = 0) { store.update(any(), any()) }
    }

    @Test
    fun `rejects armor class override greater than 30`() {
        val ex = assertThrows<ValidationException> {
            editCharacter.execute(validInput.copy(armorClassOverride = 31))
        }
        assertTrue(ex.errors.any { it.contains("Armor class", ignoreCase = true) || it.contains("Armour class", ignoreCase = true) })
        verify(exactly = 0) { store.update(any(), any()) }
    }

    @Test
    fun `allows null armor class override`() {
        every { store.update(any(), any()) } returns true

        editCharacter.execute(validInput.copy(armorClassOverride = null))

        verify { store.update(characterId, any()) }
    }
}
