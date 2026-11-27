package com.dndstats.usecases

import com.dndstats.domain.AbilityScores
import com.dndstats.domain.Character
import com.dndstats.domain.CharacterClass
import kotlin.uuid.Uuid

class CreateCharacter(private val store: CharacterStore) {
    companion object {
        val AVAILABLE_CLASSES: List<String> = CharacterClass.entries.map { it.displayName }
    }

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

    data class Output(val id: Uuid)

    fun execute(input: Input): Output {
        val errors = mutableListOf<String>()

        if (input.name.isBlank()) {
            errors.add("Name is required")
        } else if (input.name.length > 50) {
            errors.add("Name must be 50 characters or fewer")
        }

        if (input.species.length > 50) {
            errors.add("Species must be 50 characters or fewer")
        }

        if (input.characterClass.isBlank() || CharacterClass.from(input.characterClass) == null) {
            errors.add("Class must be one of the 12 classes")
        }

        if (input.level !in 1..20) {
            errors.add("Level must be between 1 and 20")
        }

        validateScore("Strength", input.abilityScores.strength, errors)
        validateScore("Dexterity", input.abilityScores.dexterity, errors)
        validateScore("Constitution", input.abilityScores.constitution, errors)
        validateScore("Intelligence", input.abilityScores.intelligence, errors)
        validateScore("Wisdom", input.abilityScores.wisdom, errors)
        validateScore("Charisma", input.abilityScores.charisma, errors)

        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }

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
            name = input.name.trim(),
            species = input.species.trim(),
            characterClass = input.characterClass.trim(),
            level = input.level,
            abilityScores = scores,
        )

        return Output(id = store.save(character))
    }

    private fun validateScore(scoreName: String, value: Int, errors: MutableList<String>) {
        if (value !in 1..30) {
            errors.add("$scoreName must be between 1 and 30")
        }
    }
}
