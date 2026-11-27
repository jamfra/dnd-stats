package com.dndstats.domain

enum class ScoreName {
    STRENGTH, DEXTERITY, CONSTITUTION, INTELLIGENCE, WISDOM, CHARISMA
}

data class ScoreResult(val score: Int, val modifier: Int)

data class AbilityScores(
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int,
) {
    companion object {
        fun default() = AbilityScores(
            strength = 15,
            dexterity = 13,
            constitution = 14,
            intelligence = 10,
            wisdom = 12,
            charisma = 8,
        )
    }

    fun get(name: ScoreName): ScoreResult {
        val score = scoreFor(name)
        return ScoreResult(score = score, modifier = modifier(score))
    }

    fun addScores(other: AbilityScores) = AbilityScores(
        strength = cap(strength + other.strength),
        dexterity = cap(dexterity + other.dexterity),
        constitution = cap(constitution + other.constitution),
        intelligence = cap(intelligence + other.intelligence),
        wisdom = cap(wisdom + other.wisdom),
        charisma = cap(charisma + other.charisma),
    )

    private fun scoreFor(name: ScoreName) = when (name) {
        ScoreName.STRENGTH -> strength
        ScoreName.DEXTERITY -> dexterity
        ScoreName.CONSTITUTION -> constitution
        ScoreName.INTELLIGENCE -> intelligence
        ScoreName.WISDOM -> wisdom
        ScoreName.CHARISMA -> charisma
    }

    private fun modifier(score: Int): Int = (score - 10).floorDiv(2)

    private fun cap(score: Int): Int = minOf(score, 30)
}
