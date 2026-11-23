package com.dndstats.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CharacterTest {

    private val ariaScores = AbilityScores(15, 8, 13, 10, 12, 14)
    private val aria = Character("Aria", "Human", "Paladin", 1, ariaScores)

    @Nested
    inner class `basic info` {
        private val sheet = aria.basicInfo()

        @Test
        fun `has name`() = assertEquals("Aria", sheet.name)

        @Test
        fun `has species`() = assertEquals("Human", sheet.species)

        @Test
        fun `has level`() = assertEquals(1, sheet.level)

        @Test
        fun `has class`() = assertEquals("Paladin", sheet.characterClass)
    }

    @Nested
    inner class `proficiency bonus by level` {
        @Test
        fun `calculates proficiency bonus according to 5e rules`() {
            assertEquals(2, Character("Aria", "Human", "Paladin", 1).proficiencyBonus())
            assertEquals(2, Character("Aria", "Human", "Paladin", 4).proficiencyBonus())
            assertEquals(3, Character("Aria", "Human", "Paladin", 5).proficiencyBonus())
            assertEquals(3, Character("Aria", "Human", "Paladin", 8).proficiencyBonus())
            assertEquals(4, Character("Aria", "Human", "Paladin", 9).proficiencyBonus())
            assertEquals(4, Character("Aria", "Human", "Paladin", 12).proficiencyBonus())
            assertEquals(5, Character("Aria", "Human", "Paladin", 13).proficiencyBonus())
            assertEquals(5, Character("Aria", "Human", "Paladin", 16).proficiencyBonus())
            assertEquals(6, Character("Aria", "Human", "Paladin", 17).proficiencyBonus())
            assertEquals(6, Character("Aria", "Human", "Paladin", 20).proficiencyBonus())
        }
    }

    @Nested
    inner class `calculated stats matching issue 1 test data` {
        @Test
        fun `returns expected hitpoints`() {
            assertEquals(11, aria.hitpoints())
        }

        @Test
        fun `returns expected armour class`() {
            assertEquals(9, aria.armourClass())
        }

        @Test
        fun `returns expected strength stat`() {
            assertEquals(
                Character.StatResult(score = 15, modifier = 2, savingThrow = 2, proficient = false),
                aria.getStat(ScoreName.STRENGTH),
            )
        }

        @Test
        fun `returns expected dexterity stat`() {
            assertEquals(
                Character.StatResult(score = 8, modifier = -1, savingThrow = -1, proficient = false),
                aria.getStat(ScoreName.DEXTERITY),
            )
        }

        @Test
        fun `returns expected constitution stat`() {
            assertEquals(
                Character.StatResult(score = 13, modifier = 1, savingThrow = 1, proficient = false),
                aria.getStat(ScoreName.CONSTITUTION),
            )
        }

        @Test
        fun `returns expected intelligence stat`() {
            assertEquals(
                Character.StatResult(score = 10, modifier = 0, savingThrow = 0, proficient = false),
                aria.getStat(ScoreName.INTELLIGENCE),
            )
        }

        @Test
        fun `returns expected wisdom stat with proficient save`() {
            assertEquals(
                Character.StatResult(score = 12, modifier = 1, savingThrow = 3, proficient = true),
                aria.getStat(ScoreName.WISDOM),
            )
        }

        @Test
        fun `returns expected charisma stat with proficient save`() {
            assertEquals(
                Character.StatResult(score = 14, modifier = 2, savingThrow = 4, proficient = true),
                aria.getStat(ScoreName.CHARISMA),
            )
        }
    }

    @Nested
    inner class `class specific calculations` {
        @Test
        fun `calculates hit points for wizard with d6 hit die`() {
            val wizard = Character("Mage", "Elf", "Wizard", 1, AbilityScores(10, 10, 14, 16, 10, 10))
            assertEquals(8, wizard.hitpoints())
        }

        @Test
        fun `calculates hit points for barbarian with d12 hit die`() {
            val barb = Character("Conan", "Human", "Barbarian", 1, AbilityScores(16, 14, 16, 8, 12, 8))
            assertEquals(15, barb.hitpoints())
        }

        @Test
        fun `marks proficient saving throws for wizard`() {
            val wizard = Character("Mage", "Elf", "Wizard", 1, AbilityScores(10, 10, 10, 16, 14, 10))
            assertEquals(
                Character.StatResult(score = 16, modifier = 3, savingThrow = 5, proficient = true),
                wizard.getStat(ScoreName.INTELLIGENCE),
            )
            assertEquals(
                Character.StatResult(score = 14, modifier = 2, savingThrow = 4, proficient = true),
                wizard.getStat(ScoreName.WISDOM),
            )
            assertEquals(
                Character.StatResult(score = 10, modifier = 0, savingThrow = 0, proficient = false),
                wizard.getStat(ScoreName.CONSTITUTION),
            )
        }
    }
}
