package com.dndstats.usecases

import com.dndstats.domain.AbilityScores
import com.dndstats.domain.Character
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.Uuid

class CreateCharacterTest {

    private val store = mockk<CharacterStore>()
    private val createCharacter = CreateCharacter(store)

    private val validInput = CreateCharacter.Input(
        name = "Aria",
        species = "Human",
        characterClass = "Paladin",
        level = 1,
        abilityScores = CreateCharacter.Input.AbilityScoreInput(15, 8, 13, 10, 12, 14),
    )

    @Test
    fun `calls save on store with the constructed character`() {
        val captured = slot<Character>()
        every { store.save(capture(captured)) } returns Uuid.random()

        createCharacter.execute(validInput)

        verify { store.save(any()) }
        assertEquals(
            Character("Aria", "Human", "Paladin", 1, AbilityScores(15, 8, 13, 10, 12, 14)),
            captured.captured,
        )
    }

    @Test
    fun `returns the id from the store`() {
        val expectedId = Uuid.parse("05b86b6c-c349-496a-b94f-a6c3d801ebf5")
        every { store.save(any()) } returns expectedId

        val result = createCharacter.execute(validInput)

        assertEquals(expectedId, result.id)
    }

    @Test
    fun `rejects empty name`() {
        val ex = assertThrows<ValidationException> {
            createCharacter.execute(validInput.copy(name = "   "))
        }
        assertTrue(ex.errors.any { it.contains("Name", ignoreCase = true) })
        verify(exactly = 0) { store.save(any()) }
    }

    @Test
    fun `rejects name exceeding 50 characters`() {
        val ex = assertThrows<ValidationException> {
            createCharacter.execute(validInput.copy(name = "a".repeat(51)))
        }
        assertTrue(ex.errors.any { it.contains("Name", ignoreCase = true) })
        verify(exactly = 0) { store.save(any()) }
    }

    @Test
    fun `rejects species exceeding 50 characters`() {
        val ex = assertThrows<ValidationException> {
            createCharacter.execute(validInput.copy(species = "a".repeat(51)))
        }
        assertTrue(ex.errors.any { it.contains("Species", ignoreCase = true) })
        verify(exactly = 0) { store.save(any()) }
    }

    @Test
    fun `rejects invalid or missing class`() {
        val ex = assertThrows<ValidationException> {
            createCharacter.execute(validInput.copy(characterClass = "InvalidClass"))
        }
        assertTrue(ex.errors.any { it.contains("Class", ignoreCase = true) })
        verify(exactly = 0) { store.save(any()) }
    }

    @Test
    fun `rejects level less than 1`() {
        val ex = assertThrows<ValidationException> {
            createCharacter.execute(validInput.copy(level = 0))
        }
        assertTrue(ex.errors.any { it.contains("Level", ignoreCase = true) })
        verify(exactly = 0) { store.save(any()) }
    }

    @Test
    fun `rejects level greater than 20`() {
        val ex = assertThrows<ValidationException> {
            createCharacter.execute(validInput.copy(level = 21))
        }
        assertTrue(ex.errors.any { it.contains("Level", ignoreCase = true) })
        verify(exactly = 0) { store.save(any()) }
    }

    @Test
    fun `rejects ability score of 0`() {
        val ex = assertThrows<ValidationException> {
            createCharacter.execute(
                validInput.copy(
                    abilityScores = validInput.abilityScores.copy(strength = 0),
                )
            )
        }
        assertTrue(ex.errors.any { it.contains("Strength", ignoreCase = true) })
        verify(exactly = 0) { store.save(any()) }
    }

    @Test
    fun `rejects ability score greater than 30`() {
        val ex = assertThrows<ValidationException> {
            createCharacter.execute(
                validInput.copy(
                    abilityScores = validInput.abilityScores.copy(dexterity = 31),
                )
            )
        }
        assertTrue(ex.errors.any { it.contains("Dexterity", ignoreCase = true) })
        verify(exactly = 0) { store.save(any()) }
    }

    @Test
    fun `collects multiple validation errors and names all fields at fault`() {
        val ex = assertThrows<ValidationException> {
            createCharacter.execute(
                validInput.copy(
                    name = "",
                    characterClass = "None",
                    level = 25,
                    abilityScores = CreateCharacter.Input.AbilityScoreInput(0, 35, 10, 10, 10, 10),
                )
            )
        }
        assertTrue(ex.errors.any { it.contains("Name", ignoreCase = true) })
        assertTrue(ex.errors.any { it.contains("Class", ignoreCase = true) })
        assertTrue(ex.errors.any { it.contains("Level", ignoreCase = true) })
        assertTrue(ex.errors.any { it.contains("Strength", ignoreCase = true) })
        assertTrue(ex.errors.any { it.contains("Dexterity", ignoreCase = true) })
        verify(exactly = 0) { store.save(any()) }
    }
}
