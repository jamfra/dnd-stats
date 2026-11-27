package com.dndstats.presentation

import com.dndstats.usecases.CreateCharacter
import com.dndstats.usecases.DeleteCharacter
import com.dndstats.usecases.EditCharacter
import com.dndstats.usecases.GetCharacterSheet
import com.dndstats.usecases.ListCharacters

data class UseCases(
    val createCharacter: CreateCharacter,
    val getCharacterSheet: GetCharacterSheet,
    val listCharacters: ListCharacters,
    val deleteCharacter: DeleteCharacter,
    val editCharacter: EditCharacter,
)
