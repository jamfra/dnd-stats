package com.dndstats.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CharacterClassTest {

    @Test
    fun `resolves class case-insensitively`() {
        assertEquals(CharacterClass.PALADIN, CharacterClass.from("paladin"))
        assertEquals(CharacterClass.PALADIN, CharacterClass.from("Paladin"))
        assertEquals(CharacterClass.PALADIN, CharacterClass.from("PALADIN"))
        assertNull(CharacterClass.from("unknown"))
    }

    @Test
    fun `has correct hit die and saving throws for all 12 classes`() {
        assertEquals(12, CharacterClass.BARBARIAN.hitDie)
        assertEquals(listOf(ScoreName.STRENGTH, ScoreName.CONSTITUTION), CharacterClass.BARBARIAN.savingThrows)

        assertEquals(8, CharacterClass.BARD.hitDie)
        assertEquals(listOf(ScoreName.DEXTERITY, ScoreName.CHARISMA), CharacterClass.BARD.savingThrows)

        assertEquals(8, CharacterClass.CLERIC.hitDie)
        assertEquals(listOf(ScoreName.WISDOM, ScoreName.CHARISMA), CharacterClass.CLERIC.savingThrows)

        assertEquals(8, CharacterClass.DRUID.hitDie)
        assertEquals(listOf(ScoreName.INTELLIGENCE, ScoreName.WISDOM), CharacterClass.DRUID.savingThrows)

        assertEquals(10, CharacterClass.FIGHTER.hitDie)
        assertEquals(listOf(ScoreName.STRENGTH, ScoreName.CONSTITUTION), CharacterClass.FIGHTER.savingThrows)

        assertEquals(8, CharacterClass.MONK.hitDie)
        assertEquals(listOf(ScoreName.STRENGTH, ScoreName.DEXTERITY), CharacterClass.MONK.savingThrows)

        assertEquals(10, CharacterClass.PALADIN.hitDie)
        assertEquals(listOf(ScoreName.WISDOM, ScoreName.CHARISMA), CharacterClass.PALADIN.savingThrows)

        assertEquals(10, CharacterClass.RANGER.hitDie)
        assertEquals(listOf(ScoreName.STRENGTH, ScoreName.DEXTERITY), CharacterClass.RANGER.savingThrows)

        assertEquals(8, CharacterClass.ROGUE.hitDie)
        assertEquals(listOf(ScoreName.DEXTERITY, ScoreName.INTELLIGENCE), CharacterClass.ROGUE.savingThrows)

        assertEquals(6, CharacterClass.SORCERER.hitDie)
        assertEquals(listOf(ScoreName.CONSTITUTION, ScoreName.CHARISMA), CharacterClass.SORCERER.savingThrows)

        assertEquals(8, CharacterClass.WARLOCK.hitDie)
        assertEquals(listOf(ScoreName.WISDOM, ScoreName.CHARISMA), CharacterClass.WARLOCK.savingThrows)

        assertEquals(6, CharacterClass.WIZARD.hitDie)
        assertEquals(listOf(ScoreName.INTELLIGENCE, ScoreName.WISDOM), CharacterClass.WIZARD.savingThrows)
    }
}
