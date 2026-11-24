package com.dndstats.usecases

import com.dndstats.gateways.CharacterStore
import java.util.UUID

class DeleteCharacter(private val store: CharacterStore) {

    data class Input(val id: UUID)

    fun execute(input: Input) {
        val deleted = store.delete(input.id)
        if (!deleted) {
            throw NoSuchElementException("No character with id: ${input.id}")
        }
    }
}
