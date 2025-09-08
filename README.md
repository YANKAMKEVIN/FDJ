# FDJ Sports (Android)

Clean, testable Android app to search football **leagues** and browse **teams** via **TheSportsDB**.

## Architecture
- **Clean Architecture**: `app` (UI) → `domain` (use cases, models) → `data` (Retrofit + Room).
- **DI**: Hilt.  
- **Result-first** flow: `Result.Success / Result.Failure` with `DomainError`.

## Features
- League **autocomplete** (case-insensitive, top 10).
- Teams in **List/Grid** (toggle).
- **Pull-to-refresh** (Material 3).
- **Skeleton** loaders, **error** & **placeholder** screens.
- Long-press **preview overlay** (animated pop + optional shimmer).
- **Offline fallback** from Room if network fails.
- **Leagues TTL** cache: 24h.

## Tech
Kotlin • Coroutines • Flow • Jetpack Compose (M3) • Hilt • Room • Retrofit • Moshi • OkHttp • Coil • Lottie

## Setup
1) Add API key in `local.properties` (uses demo dataset by default):
```
THE_SPORTS_DB_API_KEY=3
```
2) Expose in `app/build.gradle`:
```gradle
buildConfigField "String", "SPORTS_DB_API_KEY",
  "\"${properties.getProperty('THE_SPORTS_DB_API_KEY', '3')}\""
```

## Build & Run
```bash
./gradlew assembleDebug
./gradlew installDebug
```

## Tests
- **ViewModel** unit tests: MockK + Coroutines Test.  
- **Repository/DAO** local JVM tests: **Robolectric** + **Room (in-memory)** + **MockWebServer**.  
- **API contract** tests validate paths/queries.

Run all:
```bash
./gradlew test
```

## Key decisions
- **No Paging**: ≤ ~30 teams/league → simpler UI.  
- Teams sorted **anti-lexicographically**; Room stores `ordinal`, UI shows **every other** (`ordinal % 2 == 0`).  
- **Offline-first fallback** for teams; leagues refreshed on demand, cached for **24h**.

## Modules
- **app/**: Compose UI, ViewModels, app DI.
- **domain/**: models, `LeagueRepository` interface, use cases, dispatcher provider, `Result/DomainError`.
- **data/**: DTOs (Moshi), API, Room (entities/dao/db), repository impl, error mapping.

## Screenshots
![fdj_3](https://github.com/user-attachments/assets/a180903e-953e-45c8-aac5-8e65f4896b99)
![fdj_2](https://github.com/user-attachments/assets/9deff56f-e50f-438d-a66c-8d7d00f00dd5)
![fdj_1](https://github.com/user-attachments/assets/31bb41f7-1cae-4b3e-861e-00bd3f625ce7)
![fdj_5](https://github.com/user-attachments/assets/24a582fb-40cd-4192-b1c2-d90a8d881eb4)
![fdj_4](https://github.com/user-attachments/assets/51f923be-e1f2-4af5-9bee-436b477d8477)

## Future Improvements
- Add navigation to details screen.
- Dark mode support.
- UI tests.
