package com.dndstats.domain

class Character(
    private val name: String,
    private val species: String,
    private val characterClass: String,
    private val level: Int,
    private val abilityScores: AbilityScores = AbilityScores.default(),
    private val armourClassOverride: Int? = null,
) {
    data class BasicInfo(
        val name: String,
        val species: String,
        val level: Int,
        val characterClass: String,
        val armourClassOverride: Int? = null,
    )

    data class StatResult(
        val score: Int,
        val modifier: Int,
        val savingThrow: Int,
        val proficient: Boolean,
    )

    private val dndClass: CharacterClass? = CharacterClass.from(characterClass)

    fun basicInfo() = BasicInfo(
        name = name,
        species = species,
        level = level,
        characterClass = characterClass,
        armourClassOverride = armourClassOverride,
    )

    fun proficiencyBonus(): Int = ((level - 1) / 4) + 2

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

    fun armourClass(): Int = armourClassOverride ?: (abilityScores.get(ScoreName.DEXTERITY).modifier + 10)

    private fun hitDie(): Int = dndClass?.hitDie ?: 10

    private fun proficiencies(): List<ScoreName> = dndClass?.savingThrows ?: emptyList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Character) return false
        return name == other.name &&
            species == other.species &&
            characterClass == other.characterClass &&
            level == other.level &&
            abilityScores == other.abilityScores &&
            armourClassOverride == other.armourClassOverride
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + species.hashCode()
        result = 31 * result + characterClass.hashCode()
        result = 31 * result + level
        result = 31 * result + abilityScores.hashCode()
        result = 31 * result + (armourClassOverride?.hashCode() ?: 0)
        return result
    }
}
