package com.dndstats.usecases

import com.dndstats.domain.Character
import java.util.UUID

data class StoredCharacter(
    val id: UUID,
    val character: Character,
)

interface CharacterStore {
    fun save(character: Character): UUID
    fun get(id: UUID): Character
    fun getAll(): List<StoredCharacter>
    fun delete(id: UUID): Boolean
    fun update(id: UUID, character: Character): Boolean
}
