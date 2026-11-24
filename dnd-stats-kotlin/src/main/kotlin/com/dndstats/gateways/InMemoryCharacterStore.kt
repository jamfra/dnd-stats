package com.dndstats.gateways

import com.dndstats.domain.Character
import java.util.Collections
import java.util.LinkedHashMap
import java.util.UUID

class InMemoryCharacterStore : CharacterStore {
    private val characters: MutableMap<UUID, Character> =
        Collections.synchronizedMap(LinkedHashMap<UUID, Character>())

    override fun save(character: Character): UUID {
        val id = UUID.randomUUID()
        characters[id] = character
        return id
    }

    override fun get(id: UUID): Character =
        characters[id] ?: throw NoSuchElementException("No character with id: $id")

    override fun getAll(): List<StoredCharacter> = synchronized(characters) {
        characters.map { (id, character) -> StoredCharacter(id, character) }
    }

    override fun delete(id: UUID): Boolean {
        return characters.remove(id) != null
    }

    override fun update(id: UUID, character: Character): Boolean = synchronized(characters) {
        if (!characters.containsKey(id)) return false
        characters[id] = character
        true
    }
}
