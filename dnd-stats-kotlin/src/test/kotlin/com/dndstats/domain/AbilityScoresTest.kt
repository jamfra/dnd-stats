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
    fun `caps scores at 20`() {
        val scores = AbilityScores(20, 20, 20, 20, 20, 20)
        val result = scores.addScores(AbilityScores(1, 1, 1, 1, 1, 1))
        assertEquals(AbilityScores(20, 20, 20, 20, 20, 20), result)
    }

    @Nested
    inner class `get score` {
        @Test
        fun `returns value and modifier`() {
            val scores = AbilityScores.default()
            assertEquals(ScoreResult(score = 15, modifier = 2), scores.get(ScoreName.STRENGTH))
        }
    }
}
