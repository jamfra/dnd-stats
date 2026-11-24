package com.dndstats.integration

import com.dndstats.gateways.InMemoryCharacterStore
import com.dndstats.presentation.buildUseCases
import com.dndstats.presentation.configureRouting
import com.dndstats.presentation.configureSerialization
import com.dndstats.presentation.configureStatusPages
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EditCharacterIntegrationTest {

    private fun withApp(
        store: InMemoryCharacterStore = InMemoryCharacterStore(),
        block: suspend ApplicationTestBuilder.(store: InMemoryCharacterStore) -> Unit,
    ) = testApplication {
        application {
            val useCases = buildUseCases(store)
            configureSerialization()
            configureStatusPages()
            configureRouting(useCases)
        }
        block(store)
    }

    private suspend fun createCharacter(client: HttpClient): String {
        val response = client.post("/createCharacter") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("name=Aria&species=Human&characterClass=Paladin&level=1&strength=15&dexterity=8&constitution=13&intelligence=10&wisdom=12&charisma=14")
        }
        assertEquals(HttpStatusCode.Found, response.status)
        val location = response.headers[HttpHeaders.Location]
            ?: fail("Missing redirect location header")
        return location.removePrefix("/character/")
    }

    @Test
    fun `character sheet includes an edit link pointing to character edit form`() = withApp {
        val characterId = createCharacter(client)

        val response = client.get("/character/$characterId") {
            header(HttpHeaders.Accept, "text/html")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val html = response.bodyAsText()

        assertTrue(
            html.contains("href=\"/character/$characterId/edit\""),
            "Expected link to edit character form at /character/$characterId/edit",
        )
    }

    @Test
    fun `GET character edit responds with edit form containing pre-populated fields and armor class override`() = withApp {
        val characterId = createCharacter(client)

        val response = client.get("/character/$characterId/edit")
        assertEquals(HttpStatusCode.OK, response.status)
        val html = response.bodyAsText()

        assertTrue(html.contains("Edit Character"), "Expected heading 'Edit Character'")
        assertTrue(html.contains("value=\"Aria\""), "Expected pre-populated name 'Aria'")
        assertTrue(html.contains("value=\"Human\""), "Expected pre-populated species 'Human'")
        assertTrue(html.contains("Paladin"), "Expected Paladin class selected or present")
        assertTrue(html.contains("value=\"1\""), "Expected pre-populated level '1'")
        assertTrue(html.contains("value=\"15\""), "Expected pre-populated strength '15'")
        assertTrue(html.contains("value=\"8\""), "Expected pre-populated dexterity '8'")
        assertTrue(html.contains("name=\"armorClassOverride\"") || html.contains("name=\"armourClassOverride\""), "Expected armor class override field")
    }

    @Test
    fun `POST character edit updates character details and redirects to sheet with recalculated values`() = withApp {
        val characterId = createCharacter(client)

        val editResponse = client.post("/character/$characterId/edit") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("name=Aria+the+Valiant&species=Elf&characterClass=Paladin&level=5&strength=16&dexterity=14&constitution=14&intelligence=10&wisdom=12&charisma=16")
        }
        assertEquals(HttpStatusCode.Found, editResponse.status)
        assertEquals("/character/$characterId", editResponse.headers[HttpHeaders.Location])

        val sheetResponse = client.get("/character/$characterId") {
            header(HttpHeaders.Accept, "text/html")
        }
        assertEquals(HttpStatusCode.OK, sheetResponse.status)
        val html = sheetResponse.bodyAsText()

        assertTrue(html.contains("Name: Aria the Valiant"), "Expected updated name")
        assertTrue(html.contains("Species: Elf"), "Expected updated species")
        assertTrue(html.contains("Level: 5"), "Expected updated level")
        assertTrue(html.contains("Proficiency bonus: +3"), "Expected recalculated proficiency bonus +3 for level 5")
        assertTrue(html.contains("Hit points: 12"), "Expected recalculated hitpoints (10 + con mod +2 = 12)")
        assertTrue(html.contains("Armor class: 12"), "Expected recalculated armor class (10 + dex mod +2 = 12)")
        assertTrue(html.contains("Dexterity: 14 (+2)"), "Expected updated dexterity score and modifier")
    }

    @Test
    fun `POST character edit with armor class override replaces calculated armor class`() = withApp {
        val characterId = createCharacter(client)

        val editResponse = client.post("/character/$characterId/edit") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("name=Aria&species=Human&characterClass=Paladin&level=1&strength=15&dexterity=8&constitution=13&intelligence=10&wisdom=12&charisma=14&armorClassOverride=18")
        }
        assertEquals(HttpStatusCode.Found, editResponse.status)
        assertEquals("/character/$characterId", editResponse.headers[HttpHeaders.Location])

        val sheetResponse = client.get("/character/$characterId") {
            header(HttpHeaders.Accept, "text/html")
        }
        assertEquals(HttpStatusCode.OK, sheetResponse.status)
        val html = sheetResponse.bodyAsText()

        assertTrue(html.contains("Armor class: 18"), "Expected overridden armor class 18")
    }

    @Test
    fun `POST character edit with invalid inputs returns bad request naming fields at fault`() = withApp {
        val characterId = createCharacter(client)

        val editResponse = client.post("/character/$characterId/edit") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("name=&species=Human&characterClass=&level=25&strength=0&dexterity=8&constitution=13&intelligence=10&wisdom=12&charisma=14&armorClassOverride=35")
        }
        assertEquals(HttpStatusCode.BadRequest, editResponse.status)
        val body = editResponse.bodyAsText()

        assertTrue(body.contains("Name"), "Expected error mentioning Name")
        assertTrue(body.contains("Class"), "Expected error mentioning Class")
        assertTrue(body.contains("Level"), "Expected error mentioning Level")
        assertTrue(body.contains("Strength"), "Expected error mentioning Strength")
        assertTrue(body.contains("Armor class") || body.contains("Armour class"), "Expected error mentioning Armor class override")
    }
}
