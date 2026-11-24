package com.dndstats.gateways

import com.dndstats.domain.Character
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class InMemoryCharacterStoreTest {

    private fun createCharacter(name: String) = Character(
        name = name,
        species = "Human",
        characterClass = "Paladin",
        level = 1,
    )

    @Test
    fun `getAll returns empty list when store is empty`() {
        val store = InMemoryCharacterStore()
        assertTrue(store.getAll().isEmpty())
    }

    @Test
    fun `getAll returns characters in insertion order (oldest first)`() {
        val store = InMemoryCharacterStore()
        val id1 = store.save(createCharacter("Aria"))
        val id2 = store.save(createCharacter("Borin"))
        val id3 = store.save(createCharacter("Cass"))

        val all = store.getAll()
        assertEquals(3, all.size)
        assertEquals(id1, all[0].id)
        assertEquals("Aria", all[0].character.basicInfo().name)
        assertEquals(id2, all[1].id)
        assertEquals("Borin", all[1].character.basicInfo().name)
        assertEquals(id3, all[2].id)
        assertEquals("Cass", all[2].character.basicInfo().name)
    }

    @Test
    fun `delete removes character and returns true`() {
        val store = InMemoryCharacterStore()
        val id = store.save(createCharacter("Aria"))

        val deleted = store.delete(id)
        assertTrue(deleted)
        assertTrue(store.getAll().isEmpty())
        assertThrows(NoSuchElementException::class.java) {
            store.get(id)
        }
    }

    @Test
    fun `delete non-existent character returns false`() {
        val store = InMemoryCharacterStore()
        val deleted = store.delete(UUID.randomUUID())
        assertFalse(deleted)
    }
}
