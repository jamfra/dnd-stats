package com.dndstats.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CharacterTest {

    private val abilityScores = AbilityScores(15, 8, 13, 10, 12, 14)
    private val character = Character("bob", "human", "paladin", 1, abilityScores)

    @Nested
    inner class `basic info` {
        private val sheet = character.basicInfo()

        @Test
        fun `has name`() = assertEquals("bob", sheet.name)

        @Test
        fun `has species`() = assertEquals("human", sheet.species)

        @Test
        fun `has level`() = assertEquals(1, sheet.level)

        @Test
        fun `has class`() = assertEquals("paladin", sheet.characterClass)
    }

    @Test
    fun `returns stat with score and modifier`() {
        assertEquals(
            Character.StatResult(score = 15, modifier = 2, savingThrow = 2, proficient = false),
            character.getStat(ScoreName.STRENGTH),
        )
    }

    @Test
    fun `returns hitpoints`() = assertEquals(11, character.hitpoints())

    @Test
    fun `returns armour class`() = assertEquals(9, character.armourClass())
}
