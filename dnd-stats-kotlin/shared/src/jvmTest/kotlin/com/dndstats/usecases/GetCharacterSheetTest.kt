package com.dndstats.usecases

import com.dndstats.domain.AbilityScores
import com.dndstats.domain.Character
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.uuid.Uuid

class GetCharacterSheetTest {

    private val store = mockk<CharacterStore>()
    private val getCharacterSheet = GetCharacterSheet(store)
    private val mockCharacter = Character("test", "human", "paladin", 1, AbilityScores.default())

    @Test
    fun `builds sheet from character`() {
        every { store.get(any()) } returns mockCharacter

        val result = getCharacterSheet.execute(GetCharacterSheet.Input(Uuid.random()))

        assertEquals(
            GetCharacterSheet.SheetDto(
                name = "test",
                species = "human",
                level = 1,
                characterClass = "paladin",
                proficiencyBonus = 2,
                abilityScores = mapOf(
                    "strength"     to GetCharacterSheet.StatDto(15, 2, 2, false),
                    "dexterity"    to GetCharacterSheet.StatDto(13, 1, 1, false),
                    "constitution" to GetCharacterSheet.StatDto(14, 2, 2, false),
                    "intelligence" to GetCharacterSheet.StatDto(10, 0, 0, false),
                    "wisdom"       to GetCharacterSheet.StatDto(12, 1, 3, true),
                    "charisma"     to GetCharacterSheet.StatDto(8, -1, 1, true),
                ),
                hitpoints = 12,
                armourClass = 11,
                armourClassOverride = null,
                equipped = null,
                weapons = emptyList(),
            ),
            result.sheet,
        )
    }

    @Test
    fun `includes armour class override when present`() {
        val characterWithOverride = Character("test", "human", "paladin", 1, AbilityScores.default(), armourClassOverride = 18)
        every { store.get(any()) } returns characterWithOverride

        val result = getCharacterSheet.execute(GetCharacterSheet.Input(Uuid.random()))

        assertEquals(18, result.sheet.armourClassOverride)
        assertEquals(18, result.sheet.armourClass)
    }
}
