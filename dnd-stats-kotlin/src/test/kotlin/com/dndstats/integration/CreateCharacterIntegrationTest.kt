package com.dndstats.integration

import com.dndstats.gateways.InMemoryCharacterStore
import com.dndstats.presentation.buildUseCases
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
    fun `GET slash responds with create character html`() = withApp {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("My First Heading"))
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
    fun `full create and get character flow`() = withApp {
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

            val response = client.get("/character/$id")
            assertEquals(HttpStatusCode.OK, response.status)

            val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val sheet = json["sheet"]?.jsonObject ?: json

            assertEquals("test", sheet["name"]?.jsonPrimitive?.content)
            assertEquals("paladin", sheet["characterClass"]?.jsonPrimitive?.content)
            assertEquals(11, sheet["hitpoints"]?.jsonPrimitive?.int)
            assertEquals(9, sheet["armourClass"]?.jsonPrimitive?.int)
        }
    }
}
