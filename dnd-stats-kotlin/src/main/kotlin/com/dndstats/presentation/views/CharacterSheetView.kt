package com.dndstats.presentation.views

import com.dndstats.usecases.GetCharacterSheet
import kotlinx.html.*

fun HTML.characterSheetPage(sheet: GetCharacterSheet.SheetDto) {
    body {
        p {
            a(href = "/") { +"Back to character list" }
        }

        h1 { +"Character Sheet" }

        div(classes = "basic-info") {
            h2 { +"Character Info" }
            p { +"Name: ${sheet.name}" }
            p { +"Species: ${sheet.species}" }
            p { +"Class: ${sheet.characterClass}" }
            p { +"Level: ${sheet.level}" }
        }

        div(classes = "vitals") {
            h2 { +"Vitals" }
            val profSign = if (sheet.proficiencyBonus >= 0) "+${sheet.proficiencyBonus}" else "${sheet.proficiencyBonus}"
            p { +"Proficiency bonus: $profSign" }
            p { +"Hit points: ${sheet.hitpoints}" }
            p { +"Armor class: ${sheet.armourClass}" }
        }

        div(classes = "ability-scores") {
            h2 { +"Ability Scores" }
            ul {
                val scoreNames = listOf(
                    "strength" to "Strength",
                    "dexterity" to "Dexterity",
                    "constitution" to "Constitution",
                    "intelligence" to "Intelligence",
                    "wisdom" to "Wisdom",
                    "charisma" to "Charisma",
                )
                scoreNames.forEach { (key, label) ->
                    val stat = sheet.abilityScores[key]
                    if (stat != null) {
                        val modSign = if (stat.modifier >= 0) "+${stat.modifier}" else "${stat.modifier}"
                        li { +"$label: ${stat.score} ($modSign)" }
                    }
                }
            }
        }

        div(classes = "saving-throws") {
            h2 { +"Saving Throws" }
            ul {
                val scoreNames = listOf(
                    "strength" to "Strength",
                    "dexterity" to "Dexterity",
                    "constitution" to "Constitution",
                    "intelligence" to "Intelligence",
                    "wisdom" to "Wisdom",
                    "charisma" to "Charisma",
                )
                scoreNames.forEach { (key, label) ->
                    val stat = sheet.abilityScores[key]
                    if (stat != null) {
                        val saveSign = if (stat.savingThrow >= 0) "+${stat.savingThrow}" else "${stat.savingThrow}"
                        val profStr = if (stat.proficient) " (proficient)" else ""
                        li { +"$label: $saveSign$profStr" }
                    }
                }
            }
        }

        div(classes = "weapons") {
            h2 { +"Weapons" }
            p { +"No weapon equipped" }
        }
    }
}
