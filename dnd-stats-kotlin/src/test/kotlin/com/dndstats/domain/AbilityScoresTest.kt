package com.dndstats.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AbilityScoresTest {

    @Test
    fun `adds scores together`() {
        val scores = AbilityScores(0, 0, 0, 0, 0, 0)
        val result = scores.addScores(AbilityScores(1, 2, 3, 4, 5, 6))
        assertEquals(AbilityScores(1, 2, 3, 4, 5, 6), result)
    }

    @Test
    fun `caps scores at 30`() {
        val scores = AbilityScores(30, 30, 30, 30, 30, 30)
        val result = scores.addScores(AbilityScores(1, 1, 1, 1, 1, 1))
        assertEquals(AbilityScores(30, 30, 30, 30, 30, 30), result)
    }

    @Nested
    inner class `get score` {
        @Test
        fun `returns value and modifier`() {
            val scores = AbilityScores.default()
            assertEquals(ScoreResult(score = 15, modifier = 2), scores.get(ScoreName.STRENGTH))
        }

        @Test
        fun `rounds down for odd scores below 10`() {
            val scores = AbilityScores(9, 8, 1, 10, 11, 30)
            assertEquals(ScoreResult(score = 9, modifier = -1), scores.get(ScoreName.STRENGTH))
            assertEquals(ScoreResult(score = 8, modifier = -1), scores.get(ScoreName.DEXTERITY))
            assertEquals(ScoreResult(score = 1, modifier = -5), scores.get(ScoreName.CONSTITUTION))
            assertEquals(ScoreResult(score = 10, modifier = 0), scores.get(ScoreName.INTELLIGENCE))
            assertEquals(ScoreResult(score = 11, modifier = 0), scores.get(ScoreName.WISDOM))
            assertEquals(ScoreResult(score = 30, modifier = 10), scores.get(ScoreName.CHARISMA))
        }
    }
}
