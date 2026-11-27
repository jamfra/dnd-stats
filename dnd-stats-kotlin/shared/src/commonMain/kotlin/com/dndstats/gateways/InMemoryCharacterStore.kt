package com.dndstats.gateways

import com.dndstats.domain.Character
import com.dndstats.usecases.CharacterStore
import com.dndstats.usecases.StoredCharacter
import kotlin.uuid.Uuid

class InMemoryCharacterStore : CharacterStore {
    private val characters: MutableMap<Uuid, Character> = LinkedHashMap()

    override fun save(character: Character): Uuid {
        val id = Uuid.random()
        characters[id] = character
        return id
    }

    override fun get(id: Uuid): Character =
        characters[id] ?: throw NoSuchElementException("No character with id: $id")

    override fun getAll(): List<StoredCharacter> {
        return characters.map { (id, character) -> StoredCharacter(id, character) }
    }

    override fun delete(id: Uuid): Boolean {
        return characters.remove(id) != null
    }

    override fun update(id: Uuid, character: Character): Boolean {
        if (!characters.containsKey(id)) return false
        characters[id] = character
        return true
    }
}
