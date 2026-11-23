package com.dndstats.gateways

import com.dndstats.domain.Character
import java.util.UUID

/**
 * Port (interface) for character persistence.
 * Lives in the gateways layer but depends only on domain types.
 * Implementations (adapters) live in the same package.
 */
interface CharacterStore {
    fun save(character: Character): UUID
    fun get(id: UUID): Character
}
