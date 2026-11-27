package com.dndstats.integration

import com.dndstats.gateways.InMemoryCharacterStore
import com.dndstats.buildUseCases
import com.dndstats.presentation.configureRouting
import com.dndstats.presentation.configureSerialization
import com.dndstats.presentation.configureStatusPages
import com.dndstats.usecases.CreateCharacter
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateCharacterIntegrationTest {

    private fun withApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            val store = InMemoryCharacterStore()
            val useCases = buildUseCases(store)
            configureSerialization()
            configureStatusPages()
            configureRouting(useCases)
        }
        block()
    }

    @Test
    fun `GET createCharacter responds with create character html containing form fields`() = withApp {
        val response = client.get("/createCharacter")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Create Character"))
        assertTrue(body.contains("name=\"name\""))
        assertTrue(body.contains("name=\"species\""))
        assertTrue(body.contains("name=\"characterClass\""))
        assertTrue(body.contains("name=\"level\""))
        assertTrue(body.contains("name=\"strength\""))
        assertTrue(body.contains("name=\"dexterity\""))
        assertTrue(body.contains("name=\"constitution\""))
        assertTrue(body.contains("name=\"intelligence\""))
        assertTrue(body.contains("name=\"wisdom\""))
        assertTrue(body.contains("name=\"charisma\""))
    }

    @Test
    fun `POST createCharacter creates character and redirects`() = withApp {
        val response = client.post("/createCharacter") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("name=Bob&species=Human&characterClass=Paladin&level=1&strength=15&dexterity=8&constitution=13&intelligence=10&wisdom=12&charisma=14")
        }
        assertEquals(HttpStatusCode.Found, response.status)
        val location = response.headers[HttpHeaders.Location] ?: ""
        assertTrue(
            Regex("""^/character/[0-9a-f-]{36}$""").matches(location),
            "Expected UUID redirect, got: $location",
        )
    }

    @Test
    fun `POST createCharacter with invalid inputs returns bad request naming fields at fault`() = withApp {
        val response = client.post("/createCharacter") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("name=&species=Human&characterClass=&level=21&strength=0&dexterity=8&constitution=13&intelligence=10&wisdom=12&charisma=14")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Name"), "Expected error mentioning Name")
        assertTrue(body.contains("Class"), "Expected error mentioning Class")
        assertTrue(body.contains("Level"), "Expected error mentioning Level")
        assertTrue(body.contains("Strength"), "Expected error mentioning Strength")
    }

    @Test
    fun `view newly created character sheet HTML displays all sections in order with calculated stats`() {
        val store = InMemoryCharacterStore()
        testApplication {
            application {
                configureSerialization()
                configureStatusPages()
                configureRouting(buildUseCases(store))
            }

            val postResponse = client.post("/createCharacter") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("name=Aria&species=Human&characterClass=Paladin&level=1&strength=15&dexterity=8&constitution=13&intelligence=10&wisdom=12&charisma=14")
            }
            assertEquals(HttpStatusCode.Found, postResponse.status)
            val location = postResponse.headers[HttpHeaders.Location]!!

            val sheetResponse = client.get(location) {
                header(HttpHeaders.Accept, "text/html")
            }
            assertEquals(HttpStatusCode.OK, sheetResponse.status)
            val html = sheetResponse.bodyAsText()

            val basicInfoIdx = html.indexOf("Character Info")
            val vitalsIdx = html.indexOf("Vitals")
            val abilityScoresIdx = html.indexOf("Ability Scores")
            val savingThrowsIdx = html.indexOf("Saving Throws")
            val weaponsIdx = html.indexOf("Weapons")

            assertTrue(basicInfoIdx != -1)
            assertTrue(vitalsIdx > basicInfoIdx)
            assertTrue(abilityScoresIdx > vitalsIdx)
            assertTrue(savingThrowsIdx > abilityScoresIdx)
            assertTrue(weaponsIdx > savingThrowsIdx)

            assertTrue(html.contains("Name: Aria"))
            assertTrue(html.contains("Species: Human"))
            assertTrue(html.contains("Class: Paladin"))
            assertTrue(html.contains("Level: 1"))

            assertTrue(html.contains("Proficiency bonus: +2"))
            assertTrue(html.contains("Hit points: 11"))
            assertTrue(html.contains("Armor class: 9"))

            assertTrue(html.contains("Strength: 15 (+2)"))
            assertTrue(html.contains("Dexterity: 8 (-1)"))
            assertTrue(html.contains("Constitution: 13 (+1)"))
            assertTrue(html.contains("Intelligence: 10 (+0)"))
            assertTrue(html.contains("Wisdom: 12 (+1)"))
            assertTrue(html.contains("Charisma: 14 (+2)"))

            assertTrue(html.contains("Strength: +2"))
            assertTrue(html.contains("Dexterity: -1"))
            assertTrue(html.contains("Constitution: +1"))
            assertTrue(html.contains("Intelligence: +0"))
            assertTrue(html.contains("Wisdom: +3 (proficient)"))
            assertTrue(html.contains("Charisma: +4 (proficient)"))

            assertTrue(html.contains("No weapon equipped"))
        }
    }

    @Test
    fun `full create and get character flow via JSON API`() {
        val store = InMemoryCharacterStore()
        val useCases = buildUseCases(store)
        val id = useCases.createCharacter.execute(
            CreateCharacter.Input(
                name = "test",
                species = "human",
                characterClass = "paladin",
                level = 1,
                abilityScores = CreateCharacter.Input.AbilityScoreInput(15, 8, 13, 10, 12, 14),
            )
        ).id

        testApplication {
            application {
                configureSerialization()
                configureStatusPages()
                configureRouting(buildUseCases(store))
            }

            val response = client.get("/character/$id") {
                header(HttpHeaders.Accept, "application/json")
            }
            assertEquals(HttpStatusCode.OK, response.status)

            val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val sheet = json["sheet"]?.jsonObject ?: json

            assertEquals("test", sheet["name"]?.jsonPrimitive?.content)
            assertEquals("paladin", sheet["characterClass"]?.jsonPrimitive?.content)
            assertEquals(2, sheet["proficiencyBonus"]?.jsonPrimitive?.int)
            assertEquals(11, sheet["hitpoints"]?.jsonPrimitive?.int)
            assertEquals(9, sheet["armourClass"]?.jsonPrimitive?.int)
        }
    }
}
