package com.dndstats.gateways

import com.dndstats.domain.Character
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory adapter for [CharacterStore].
 * Thread-safe via ConcurrentHashMap.
 */
class InMemoryCharacterStore : CharacterStore {
    private val characters = ConcurrentHashMap<UUID, Character>()

    override fun save(character: Character): UUID {
        val id = UUID.randomUUID()
        characters[id] = character
        return id
    }

    override fun get(id: UUID): Character =
        characters[id] ?: throw NoSuchElementException("No character with id: $id")
}
