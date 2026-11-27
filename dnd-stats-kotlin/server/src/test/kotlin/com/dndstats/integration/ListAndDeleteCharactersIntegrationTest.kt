package com.dndstats.integration

import com.dndstats.gateways.InMemoryCharacterStore
import com.dndstats.buildUseCases
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

class ListAndDeleteCharactersIntegrationTest {

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

    private suspend fun createCharacter(
        client: HttpClient,
        name: String,
        species: String,
        characterClass: String,
        level: Int,
    ): String {
        val response = client.post("/createCharacter") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("name=$name&species=$species&characterClass=$characterClass&level=$level&strength=10&dexterity=10&constitution=10&intelligence=10&wisdom=10&charisma=10")
        }
        assertEquals(HttpStatusCode.Found, response.status)
        val location = response.headers[HttpHeaders.Location]
            ?: fail("Missing redirect location header")
        return location.removePrefix("/character/")
    }

    @Test
    fun `empty state displays no characters yet and link to create character`() = withApp {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        val html = response.bodyAsText()

        assertTrue(html.contains("No characters yet"), "Expected empty state message 'No characters yet'")
        assertTrue(
            html.contains("href=\"/createCharacter\"") || html.contains("href=\"/character/new\""),
            "Expected link to create character form",
        )
    }

    @Test
    fun `list content shows characters in creation order with name class level and sheet link`() = withApp {
        val ariaId = createCharacter(client, "Aria", "Human", "Paladin", 1)
        val borinId = createCharacter(client, "Borin", "Dwarf", "Fighter", 5)
        val cassId = createCharacter(client, "Cass", "Elf", "Wizard", 3)

        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        val html = response.bodyAsText()

        val ariaIdx = html.indexOf("Aria")
        val borinIdx = html.indexOf("Borin")
        val cassIdx = html.indexOf("Cass")

        assertTrue(ariaIdx != -1, "Expected Aria in list")
        assertTrue(borinIdx != -1, "Expected Borin in list")
        assertTrue(cassIdx != -1, "Expected Cass in list")

        assertTrue(ariaIdx < borinIdx, "Expected Aria to appear before Borin (creation order)")
        assertTrue(borinIdx < cassIdx, "Expected Borin to appear before Cass (creation order)")

        assertTrue(html.contains("Paladin"), "Expected Paladin class")
        assertTrue(html.contains("Fighter"), "Expected Fighter class")
        assertTrue(html.contains("Wizard"), "Expected Wizard class")

        assertTrue(html.contains("href=\"/character/$ariaId\""), "Expected link to Aria's sheet")
        assertTrue(html.contains("href=\"/character/$borinId\""), "Expected link to Borin's sheet")
        assertTrue(html.contains("href=\"/character/$cassId\""), "Expected link to Cass's sheet")

        val sheetResponse = client.get("/character/$ariaId") {
            header(HttpHeaders.Accept, "text/html")
        }
        assertEquals(HttpStatusCode.OK, sheetResponse.status)
        assertTrue(sheetResponse.bodyAsText().contains("Name: Aria"))
    }

    @Test
    fun `delete confirmation page shows confirmation prompt with character name and cancel link`() = withApp {
        val ariaId = createCharacter(client, "Aria", "Human", "Paladin", 1)

        val response = client.get("/character/$ariaId/delete")
        assertEquals(HttpStatusCode.OK, response.status)
        val html = response.bodyAsText()

        assertTrue(
            html.contains("Delete Aria? This cannot be undone."),
            "Expected confirmation message 'Delete Aria? This cannot be undone.'",
        )
        assertTrue(html.contains("href=\"/\""), "Expected cancel link back to list")
        assertTrue(
            html.contains("action=\"/character/$ariaId/delete\"") && html.contains("method=\"post\""),
            "Expected form to post delete confirmation",
        )
    }

    @Test
    fun `cancel delete keeps character in list`() = withApp {
        val ariaId = createCharacter(client, "Aria", "Human", "Paladin", 1)

        val confirmResponse = client.get("/character/$ariaId/delete")
        assertEquals(HttpStatusCode.OK, confirmResponse.status)
        assertTrue(confirmResponse.bodyAsText().contains("href=\"/\""), "Expected cancel link back to list")

        val listResponse = client.get("/")
        assertEquals(HttpStatusCode.OK, listResponse.status)
        assertTrue(listResponse.bodyAsText().contains("Aria"))
    }

    @Test
    fun `confirm delete removes character immediately from list`() = withApp {
        val ariaId = createCharacter(client, "Aria", "Human", "Paladin", 1)
        createCharacter(client, "Borin", "Dwarf", "Fighter", 5)

        val deleteResponse = client.post("/character/$ariaId/delete")
        assertEquals(HttpStatusCode.Found, deleteResponse.status)
        assertEquals("/", deleteResponse.headers[HttpHeaders.Location])

        val listResponse = client.get("/")
        assertEquals(HttpStatusCode.OK, listResponse.status)
        val html = listResponse.bodyAsText()

        assertFalse(html.contains("Aria"), "Expected Aria to be deleted from list")
        assertTrue(html.contains("Borin"), "Expected Borin to remain in list")
    }

    @Test
    fun `opening deleted character sheet shows Character not found and link back to list`() = withApp {
        val ariaId = createCharacter(client, "Aria", "Human", "Paladin", 1)

        client.post("/character/$ariaId/delete")

        val sheetResponse = client.get("/character/$ariaId") {
            header(HttpHeaders.Accept, "text/html")
        }
        assertEquals(HttpStatusCode.NotFound, sheetResponse.status)
        val html = sheetResponse.bodyAsText()

        assertTrue(html.contains("Character not found"), "Expected 'Character not found'")
        assertTrue(html.contains("href=\"/\""), "Expected link back to character list")
    }

    @Test
    fun `new character appears in list immediately after creation without restart`() = withApp {
        val listBefore = client.get("/").bodyAsText()
        assertFalse(listBefore.contains("Aria"))

        createCharacter(client, "Aria", "Human", "Paladin", 1)

        val listAfter = client.get("/").bodyAsText()
        assertTrue(listAfter.contains("Aria"))
    }

    @Test
    fun `server restart leaves list empty`() {
        val store1 = InMemoryCharacterStore()
        withApp(store1) {
            createCharacter(client, "Aria", "Human", "Paladin", 1)
            assertTrue(client.get("/").bodyAsText().contains("Aria"))
        }

        val store2 = InMemoryCharacterStore()
        withApp(store2) {
            val listAfterRestart = client.get("/").bodyAsText()
            assertTrue(listAfterRestart.contains("No characters yet"))
            assertFalse(listAfterRestart.contains("Aria"))
        }
    }
}
