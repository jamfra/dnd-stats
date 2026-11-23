# dnd-stats

A D&D 5e character statistics and sheet management application built using Clean Architecture principles.

The service provides domain models and HTTP endpoints to create characters and calculate character sheets, including ability score modifiers, saving throws, armour class, and hit points based on 5e rules.

## Repository Overview

This repository contains two implementations:

- [dnd-stats-kotlin](dnd-stats-kotlin/): The Kotlin (Ktor / JVM 21) implementation. This is the primary target implementation going forward.
- [dnd-stats-typescript](dnd-stats-typescript/): The original TypeScript (Express / Node.js) implementation, currently retained for reference while transitioning to Kotlin.

## Architecture

Both projects follow Clean Architecture with strict separation between layers:

- **Domain**: Pure business rules and entities (`Character`, `AbilityScores`) with no framework dependencies.
- **Gateways**: Interfaces (ports) and adapters for persistence (`CharacterStore`, `InMemoryCharacterStore`).
- **Use Cases**: Application workflows (`CreateCharacter`, `GetCharacterSheet`).
- **Presentation**: HTTP routing, HTML forms, and JSON serialization.

## Quick Start

### Kotlin (Primary)

Requires JDK 21+.

```bash
cd dnd-stats-kotlin
./gradlew test
./gradlew run
```

See [dnd-stats-kotlin/README.md](dnd-stats-kotlin/README.md) for detailed documentation and API specifications.

### TypeScript (Legacy)

Requires Node.js 20+.

```bash
cd dnd-stats-typescript
npm install
npm test
npm run dev
```

See [dnd-stats-typescript/README.md](dnd-stats-typescript/README.md) for details on the TypeScript implementation.
