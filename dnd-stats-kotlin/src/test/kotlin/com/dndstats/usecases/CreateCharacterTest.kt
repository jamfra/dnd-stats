package com.dndstats.usecases

import com.dndstats.domain.AbilityScores
import com.dndstats.domain.Character
import com.dndstats.gateways.CharacterStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateCharacterTest {

    private val store = mockk<CharacterStore>()
    private val createCharacter = CreateCharacter(store)

    private val input = CreateCharacter.Input(
        name = "",
        species = "",
        characterClass = "",
        level = 0,
        abilityScores = CreateCharacter.Input.AbilityScoreInput(0, 0, 0, 0, 0, 0),
    )

    @Test
    fun `calls save on store with the constructed character`() {
        val captured = slot<Character>()
        every { store.save(capture(captured)) } returns UUID.randomUUID()

        createCharacter.execute(input)

        verify { store.save(any()) }
        assertEquals(Character("", "", "", 0, AbilityScores(0, 0, 0, 0, 0, 0)), captured.captured)
    }

    @Test
    fun `returns the id from the store`() {
        val expectedId = UUID.fromString("05b86b6c-c349-496a-b94f-a6c3d801ebf5")
        every { store.save(any()) } returns expectedId

        val result = createCharacter.execute(input)

        assertEquals(expectedId, result.id)
    }
}
