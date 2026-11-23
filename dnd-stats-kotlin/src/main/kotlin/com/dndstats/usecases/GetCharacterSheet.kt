package com.dndstats.usecases

import com.dndstats.domain.ScoreName
import com.dndstats.gateways.CharacterStore
import kotlinx.serialization.Serializable
import java.util.UUID

class GetCharacterSheet(private val store: CharacterStore) {
    data class Input(val id: UUID)

    data class Output(val sheet: SheetDto)

    @Serializable
    data class SheetDto(
        val name: String,
        val species: String,
        val level: Int,
        val characterClass: String,
        val proficiencyBonus: Int,
        val abilityScores: Map<String, StatDto>,
        val hitpoints: Int,
        val armourClass: Int,
        val equipped: String? = null,
        val weapons: List<String> = emptyList(),
    )

    @Serializable
    data class StatDto(
        val score: Int,
        val modifier: Int,
        val savingThrow: Int,
        val proficient: Boolean,
    )

    fun execute(input: Input): Output {
        val character = store.get(input.id)
        val info = character.basicInfo()

        val abilityScores = ScoreName.entries.associate { scoreName ->
            val stat = character.getStat(scoreName)
            scoreName.name.lowercase() to StatDto(
                score = stat.score,
                modifier = stat.modifier,
                savingThrow = stat.savingThrow,
                proficient = stat.proficient,
            )
        }

        return Output(
            sheet = SheetDto(
                name = info.name,
                species = info.species,
                level = info.level,
                characterClass = info.characterClass,
                proficiencyBonus = character.proficiencyBonus(),
                abilityScores = abilityScores,
                hitpoints = character.hitpoints(),
                armourClass = character.armourClass(),
                equipped = null,
                weapons = emptyList(),
            )
        )
    }
}
