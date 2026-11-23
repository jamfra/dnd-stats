# dnd-stats-typescript

TypeScript implementation of the D&D 5e character statistics and character sheet service, built adhering to Clean Architecture principles.

## Tech Stack

- **Runtime**: Node.js (v20+)
- **Language**: TypeScript 5+
- **Web Framework**: Express 5
- **Testing**: Jest with `@swc/jest` transform, Supertest, Cheerio
- **Execution**: `tsx` for TypeScript execution and file watching

## Project Structure

```
dnd-stats-typescript/
├── src/
│   ├── app.ts                                 # Server bootstrap & Express setup
│   ├── useCases.ts                            # Composition root wiring dependencies
│   ├── domain/                                # Enterprise business rules (pure TS)
│   │   ├── abilityScores.ts                   # AbilityScores value object and modifier math
│   │   ├── abilityScores.test.ts
│   │   ├── character.ts                       # Character aggregate root
│   │   └── character.test.ts
│   ├── gateways/                              # Interface adapters / ports
│   │   └── characterStore/
│   │       ├── characterStore.ts              # Port interface
│   │       └── inMemoryStore.ts               # In-memory implementation
│   ├── useCases/                              # Application business rules
│   │   ├── createCharacter.ts                 # CreateCharacter use case
│   │   ├── createCharacter.test.ts
│   │   ├── getCharacterSheet.ts               # GetCharacterSheet use case
│   │   └── getCharacterSheet.test.ts
│   ├── presenters/                            # HTTP controllers / routes
│   │   ├── routes.ts                          # Express route definitions
│   │   └── routes.test.ts
│   ├── utils/
│   │   └── result.ts                          # Result<T> functional return wrapper
│   └── views/
│       └── index.html                         # Basic character creation form HTML
├── test/
│   └── createCharacter.int.test.ts            # End-to-end integration test
├── package.json
└── tsconfig.json
```

## Getting Started

### Prerequisites

- Node.js 20 or higher
- npm 9 or higher

### Installation

```bash
npm install
```

### Running the Application

To run the application locally with hot reload:

```bash
npm run dev
```

The server listens on `http://localhost:3000`.

To build TypeScript into JavaScript:

```bash
npm run build
```

### Running Tests

Run all unit and integration tests:

```bash
npm test
```

Run only unit tests:

```bash
npm run test-unit
```

## API Specification

### 1. Character Creation Page
- **Method**: `GET /`
- **Description**: Serves the initial HTML page containing a simple form to create a character.
- **Response**: `200 OK` (`text/html`)

### 2. Create Character
- **Method**: `POST /createCharacter`
- **Content-Type**: `application/x-www-form-urlencoded`
- **Request Body**:
  ```
  name=Bob
  ```
- **Behavior**: Persists a new character using the `CreateCharacter` use case and redirects to the character sheet.
- **Response**: `302 Found` with header `Location: /character/{id}`

### 3. Get Character Sheet
- **Method**: `GET /character/:id`
- **Description**: Fetches the computed D&D 5e sheet for the specified character ID.
- **Response**: `200 OK` (`application/json`)
- **Example Response Body**:
  ```json
  {
    "success": true,
    "value": {
      "sheet": {
        "name": "Bob",
        "species": "human",
        "class": "paladin",
        "level": 1,
        "abilityScores": {
          "strength": {
            "score": 15,
            "modifier": 2,
            "savingThrow": 2,
            "proficient": false
          },
          "dexterity": {
            "score": 8,
            "modifier": -1,
            "savingThrow": -1,
            "proficient": false
          },
          "constitution": {
            "score": 13,
            "modifier": 1,
            "savingThrow": 1,
            "proficient": false
          },
          "intelligence": {
            "score": 10,
            "modifier": 0,
            "savingThrow": 0,
            "proficient": false
          },
          "wisdom": {
            "score": 12,
            "modifier": 1,
            "savingThrow": 3,
            "proficient": true
          },
          "charisma": {
            "score": 14,
            "modifier": 2,
            "savingThrow": 4,
            "proficient": true
          }
        },
        "hitpoints": 11,
        "armourClass": 9,
        "equipped": null,
        "weapons": []
      }
    }
  }
  ```

## Design Notes

- **Clean Architecture**: Domain entities have zero external dependencies. Use cases depend only on the domain and gateway interfaces. Express routes depend only on use cases.
- **Result Type**: Use cases return a `Result<T>` structure (`{ success: boolean, value: T }`) rather than throwing exceptions or returning raw outputs.
- **Store Port**: Persistence is abstracted via `CharacterStore`. The default implementation is `InMemoryCharacterStore`, which uses an in-memory object map.
