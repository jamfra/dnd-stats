package com.dndstats.presentation

import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.GetCharacterSheet
import com.dndstats.gateways.CharacterStore

data class UseCases(
    val createCharacter: CreateCharacter,
    val getCharacterSheet: GetCharacterSheet,
)

fun buildUseCases(store: CharacterStore) = UseCases(
    createCharacter = CreateCharacter(store),
    getCharacterSheet = GetCharacterSheet(store),
)
