package com.dndstats.usecases

import com.dndstats.domain.Character
import kotlin.uuid.Uuid

data class StoredCharacter(
    val id: Uuid,
    val character: Character,
)

interface CharacterStore {
    fun save(character: Character): Uuid
    fun get(id: Uuid): Character
    fun getAll(): List<StoredCharacter>
    fun delete(id: Uuid): Boolean
    fun update(id: Uuid, character: Character): Boolean
}
