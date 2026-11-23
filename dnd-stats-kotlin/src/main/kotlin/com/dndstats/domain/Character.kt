package com.dndstats.domain

/**
 * Aggregate root for a D&D character.
 * Pure Kotlin — no framework dependencies.
 */
class Character(
    private val name: String,
    private val species: String,
    private val characterClass: String,
    private val level: Int,
    private val abilityScores: AbilityScores = AbilityScores.default(),
) {
    data class BasicInfo(
        val name: String,
        val species: String,
        val level: Int,
        val characterClass: String,
    )

    data class StatResult(
        val score: Int,
        val modifier: Int,
        val savingThrow: Int,
        val proficient: Boolean,
    )

    fun basicInfo() = BasicInfo(
        name = name,
        species = species,
        level = level,
        characterClass = characterClass,
    )

    fun getStat(name: ScoreName): StatResult {
        val isProficient = proficiencies().contains(name)
        val scoreResult = abilityScores.get(name)
        val savingThrow = if (isProficient) scoreResult.modifier + proficiencyBonus() else scoreResult.modifier

        return StatResult(
            score = scoreResult.score,
            modifier = scoreResult.modifier,
            savingThrow = savingThrow,
            proficient = isProficient,
        )
    }

    fun hitpoints(): Int = hitDie() + abilityScores.get(ScoreName.CONSTITUTION).modifier

    fun armourClass(): Int = abilityScores.get(ScoreName.DEXTERITY).modifier + 10

    private fun hitDie(): Int = 10

    private fun proficiencies(): List<ScoreName> = when (characterClass) {
        "paladin" -> listOf(ScoreName.CHARISMA, ScoreName.WISDOM)
        else -> emptyList()
    }

    private fun proficiencyBonus(): Int = 2

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Character) return false
        return name == other.name && abilityScores == other.abilityScores
    }

    override fun hashCode(): Int = 31 * name.hashCode() + abilityScores.hashCode()
}
