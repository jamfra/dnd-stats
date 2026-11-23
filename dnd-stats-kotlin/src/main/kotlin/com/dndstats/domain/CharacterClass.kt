package com.dndstats.domain

enum class CharacterClass(
    val displayName: String,
    val hitDie: Int,
    val savingThrows: List<ScoreName>,
) {
    BARBARIAN("Barbarian", 12, listOf(ScoreName.STRENGTH, ScoreName.CONSTITUTION)),
    BARD("Bard", 8, listOf(ScoreName.DEXTERITY, ScoreName.CHARISMA)),
    CLERIC("Cleric", 8, listOf(ScoreName.WISDOM, ScoreName.CHARISMA)),
    DRUID("Druid", 8, listOf(ScoreName.INTELLIGENCE, ScoreName.WISDOM)),
    FIGHTER("Fighter", 10, listOf(ScoreName.STRENGTH, ScoreName.CONSTITUTION)),
    MONK("Monk", 8, listOf(ScoreName.STRENGTH, ScoreName.DEXTERITY)),
    PALADIN("Paladin", 10, listOf(ScoreName.WISDOM, ScoreName.CHARISMA)),
    RANGER("Ranger", 10, listOf(ScoreName.STRENGTH, ScoreName.DEXTERITY)),
    ROGUE("Rogue", 8, listOf(ScoreName.DEXTERITY, ScoreName.INTELLIGENCE)),
    SORCERER("Sorcerer", 6, listOf(ScoreName.CONSTITUTION, ScoreName.CHARISMA)),
    WARLOCK("Warlock", 8, listOf(ScoreName.WISDOM, ScoreName.CHARISMA)),
    WIZARD("Wizard", 6, listOf(ScoreName.INTELLIGENCE, ScoreName.WISDOM));

    companion object {
        fun from(value: String): CharacterClass? =
            entries.firstOrNull {
                it.name.equals(value, ignoreCase = true) || it.displayName.equals(value, ignoreCase = true)
            }
    }
}
