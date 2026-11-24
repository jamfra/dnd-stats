# dnd-stats-kotlin

Kotlin implementation of the D&D 5e character statistics and character sheet service, built adhering to Clean Architecture principles.

## Tech Stack

- **Runtime**: JVM 21
- **Language**: Kotlin 2.0.21
- **Build Tool**: Gradle (Kotlin DSL)
- **Web Framework**: Ktor 2.3.12 (Netty engine)
- **Serialization**: `kotlinx.serialization` (JSON)
- **HTML Templating**: `kotlinx.html` (Ktor HTML builder DSL)
- **Logging**: Logback
- **Testing**: JUnit 5 (Jupiter), MockK, Ktor Server Test Host

## Project Structure

```
dnd-stats-kotlin/
├── build.gradle.kts
├── settings.gradle.kts
└── src/
    ├── main/
    │   ├── kotlin/com/dndstats/
    │   │   ├── Application.kt                       # Application entrypoint & Netty server
    │   │   ├── domain/                              # Pure domain entities & value objects
    │   │   │   ├── AbilityScores.kt                 # AbilityScores, ScoreName, and math
    │   │   │   └── Character.kt                     # Character aggregate root
    │   │   ├── gateways/                            # Persistence ports & adapters
    │   │   │   ├── CharacterStore.kt                # Persistence interface (port)
    │   │   │   └── InMemoryCharacterStore.kt        # Thread-safe in-memory adapter
    │   │   ├── presentation/                        # HTTP delivery layer (Ktor)
    │   │   │   ├── Routing.kt                       # Route definitions
    │   │   │   ├── Serialization.kt                 # ContentNegotiation setup
    │   │   │   ├── StatusPages.kt                   # Exception handling & status codes
    │   │   │   └── UseCases.kt                      # Composition root wiring dependencies
    │   │   └── usecases/                            # Application use cases
    │   │       ├── CreateCharacter.kt               # CreateCharacter use case
    │   │       └── GetCharacterSheet.kt             # GetCharacterSheet use case
    │   └── resources/
    │       └── logback.xml                          # Logging configuration
    └── test/
        └── kotlin/com/dndstats/
            ├── domain/                              # Domain unit tests
            │   ├── AbilityScoresTest.kt
            │   └── CharacterTest.kt
            ├── usecases/                            # Use case unit tests
            │   ├── CreateCharacterTest.kt
            │   └── GetCharacterSheetTest.kt
            └── integration/                         # Integration test with Ktor testApplication
                └── CreateCharacterIntegrationTest.kt
```

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 21 or higher
- Gradle (or use the Gradle wrapper once generated)

### Running the Application

To start the server using Gradle:

```bash
gradle run
```
Or with the wrapper:
```bash
./gradlew run
```

The server starts on `http://localhost:3000`.

### Running Tests

Execute the full test suite with Gradle:

```bash
gradle test
```
Or with the wrapper:
```bash
./gradlew test
```

Test reports are generated at `build/reports/tests/test/index.html`.

## API Specification

### 1. Character List Page (Main Entrypoint)
- **Method**: `GET /`
- **Description**: Returns HTML listing all saved characters in creation order (oldest first). If no characters exist, displays "No characters yet".
- **Response**: `200 OK` (`text/html` or `application/json`)

### 2. Character Creation Page
- **Method**: `GET /createCharacter`
- **Description**: Returns HTML containing the character creation form rendered via Ktor HTML builder.
- **Response**: `200 OK` (`text/html`)

### 3. Create Character
- **Method**: `POST /createCharacter`
- **Content-Type**: `application/x-www-form-urlencoded`
- **Request Body**:
  ```
  name=Bob
  ```
- **Behavior**: Persists a character and redirects to the character sheet endpoint.
- **Response**: `302 Found` with header `Location: /character/{id}`

### 4. Get Character Sheet
- **Method**: `GET /character/{id}`
- **Description**: Fetches the computed D&D 5e sheet for the specified UUID.
- **Response**: `200 OK` (`application/json` or `text/html`)

### 5. Delete Character Confirmation Page
- **Method**: `GET /character/{id}/delete`
- **Description**: Shows confirmation prompt "Delete {name}? This cannot be undone." with confirm and cancel options.
- **Response**: `200 OK` (`text/html`)

### 6. Delete Character
- **Method**: `POST /character/{id}/delete`
- **Behavior**: Deletes the specified character and redirects to the character list page `/`.
- **Response**: `302 Found` with header `Location: /`

### 7. Error Responses
Configured via Ktor `StatusPages`:
- **400 Bad Request**: Returned if `{id}` is not a valid UUID format:
  ```
  Invalid character id
  ```
- **404 Not Found**: Returned if the character ID does not exist in the store:
  - If `Accept: text/html`: Displays "Character not found" with a link back to `/`.
  - If `Accept: application/json`:
    ```json
    {
      "error": "No character with id: <uuid>"
    }
    ```

## Design Notes

- **Clean Architecture**: Domain classes (`Character`, `AbilityScores`) and use cases have no framework dependencies.
- **Concurrency**: `InMemoryCharacterStore` utilizes `java.util.concurrent.ConcurrentHashMap` to ensure thread safety across Netty worker threads.
- **Idiomatic Kotlin**: Implements `enum class ScoreName`, `data class` for DTOs and value types, default parameter values, and standard library collection operations.
- **Differences from TypeScript Implementation**:
  - The response for `GET /character/{id}` returns the sheet DTO directly rather than nested inside a `{ "success": true, "value": { "sheet": ... } }` wrapper.
  - The character class field in the JSON response is named `"characterClass"` (TypeScript outputs `"class"`).
  - Status pages and UUID validation return explicit 404 and 400 responses.
