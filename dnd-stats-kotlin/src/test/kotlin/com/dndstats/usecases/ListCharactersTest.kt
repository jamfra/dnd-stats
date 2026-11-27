package com.dndstats.usecases

import com.dndstats.domain.Character
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class ListCharactersTest {

    private val store = mockk<CharacterStore>()
    private val listCharacters = ListCharacters(store)

    @Test
    fun `returns empty list when store has no characters`() {
        every { store.getAll() } returns emptyList()

        val result = listCharacters.execute()

        assertTrue(result.characters.isEmpty())
    }

    @Test
    fun `returns characters in creation order with summary fields`() {
        val id1 = UUID.randomUUID()
        val id2 = UUID.randomUUID()
        val char1 = Character("Aria", "Human", "Paladin", 1)
        val char2 = Character("Borin", "Dwarf", "Fighter", 5)

        every { store.getAll() } returns listOf(
            StoredCharacter(id1, char1),
            StoredCharacter(id2, char2),
        )

        val result = listCharacters.execute()

        assertEquals(2, result.characters.size)
        assertEquals(
            ListCharacters.CharacterSummary(
                id = id1.toString(),
                name = "Aria",
                characterClass = "Paladin",
                level = 1,
            ),
            result.characters[0],
        )
        assertEquals(
            ListCharacters.CharacterSummary(
                id = id2.toString(),
                name = "Borin",
                characterClass = "Fighter",
                level = 5,
            ),
            result.characters[1],
        )
    }
}
