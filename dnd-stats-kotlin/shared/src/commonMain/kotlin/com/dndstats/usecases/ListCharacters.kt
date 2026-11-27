package com.dndstats.usecases

import kotlinx.serialization.Serializable

class ListCharacters(private val store: CharacterStore) {

    data class Output(val characters: List<CharacterSummary>)

    @Serializable
    data class CharacterSummary(
        val id: String,
        val name: String,
        val characterClass: String,
        val level: Int,
    )

    fun execute(): Output {
        val summaries = store.getAll().map { stored ->
            val info = stored.character.basicInfo()
            CharacterSummary(
                id = stored.id.toString(),
                name = info.name,
                characterClass = info.characterClass,
                level = info.level,
            )
        }
        return Output(characters = summaries)
    }
}
