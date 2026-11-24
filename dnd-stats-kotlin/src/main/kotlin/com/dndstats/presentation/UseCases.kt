package com.dndstats.presentation

import com.dndstats.gateways.CharacterStore
import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.DeleteCharacter
import com.dndstats.usecases.GetCharacterSheet
import com.dndstats.usecases.ListCharacters

data class UseCases(
    val createCharacter: CreateCharacter,
    val getCharacterSheet: GetCharacterSheet,
    val listCharacters: ListCharacters,
    val deleteCharacter: DeleteCharacter,
)

fun buildUseCases(store: CharacterStore) = UseCases(
    createCharacter = CreateCharacter(store),
    getCharacterSheet = GetCharacterSheet(store),
    listCharacters = ListCharacters(store),
    deleteCharacter = DeleteCharacter(store),
)
