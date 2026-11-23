package com.dndstats.usecases

import com.dndstats.domain.AbilityScores
import com.dndstats.domain.Character
import com.dndstats.gateways.CharacterStore
import java.util.UUID

/**
 * Use case: create and persist a new character.
 * Depends only on domain types and the [CharacterStore] port.
 */
class CreateCharacter(private val store: CharacterStore) {
    data class Input(
        val name: String,
        val species: String,
        val characterClass: String,
        val level: Int,
        val abilityScores: AbilityScoreInput,
    ) {
        data class AbilityScoreInput(
            val strength: Int,
            val dexterity: Int,
            val constitution: Int,
            val intelligence: Int,
            val wisdom: Int,
            val charisma: Int,
        )
    }

    data class Output(val id: UUID)

    fun execute(input: Input): Output {
        val scores = input.abilityScores.let {
            AbilityScores(
                strength = it.strength,
                dexterity = it.dexterity,
                constitution = it.constitution,
                intelligence = it.intelligence,
                wisdom = it.wisdom,
                charisma = it.charisma,
            )
        }

        val character = Character(
            name = input.name,
            species = input.species,
            characterClass = input.characterClass,
            level = input.level,
            abilityScores = scores,
        )

        return Output(id = store.save(character))
    }
}
