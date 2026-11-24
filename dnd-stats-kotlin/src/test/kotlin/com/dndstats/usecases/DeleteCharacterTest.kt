package com.dndstats.usecases

import com.dndstats.gateways.CharacterStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class DeleteCharacterTest {

    private val store = mockk<CharacterStore>()
    private val deleteCharacter = DeleteCharacter(store)

    @Test
    fun `calls delete on store with given id`() {
        val id = UUID.randomUUID()
        every { store.delete(id) } returns true

        deleteCharacter.execute(DeleteCharacter.Input(id))

        verify { store.delete(id) }
    }

    @Test
    fun `throws NoSuchElementException when character to delete is not found`() {
        val id = UUID.randomUUID()
        every { store.delete(id) } returns false

        assertThrows<NoSuchElementException> {
            deleteCharacter.execute(DeleteCharacter.Input(id))
        }
    }
}
