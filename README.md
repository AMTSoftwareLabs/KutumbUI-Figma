# Kutumb Android App — Jetpack Compose

Complete Android implementation of the Kutumb family management app.

## File Structure

```
android/app/src/main/java/com/kutumb/app/
│
├── KutumbApplication.kt          — Hilt application class
├── MainActivity.kt               — Single activity, sets theme from DataStore
│
├── data/
│   ├── model/Entities.kt         — 12 Room @Entity data classes
│   ├── dao/AppDao.kt             — All DAOs with Flow<List<T>> queries
│   ├── database/KutumbDatabase.kt
│   └── repository/AppRepository.kt
│
├── di/
│   └── AppModule.kt              — Hilt: Room, DataStore providers
│
├── domain/
│   └── LoyaltyEngine.kt          — Score computation + LoyaltyLevel enum + SeedData
│
├── ui/
│   ├── theme/
│   │   ├── Color.kt              — All color constants + SeedTheme enum (6 themes)
│   │   ├── Type.kt               — KutumbTypography
│   │   └── Theme.kt              — KutumbTheme composable + LocalSeedTheme
│   │
│   ├── navigation/
│   │   └── KutumbNav.kt          — NavHost, BottomNav, Screen sealed class
│   │
│   ├── components/
│   │   └── SharedComponents.kt   — KutumbTopBar, DarkHeroSection, HeroStatBox,
│   │                               LeftBorderCard, GradientButton, MemberAvatar,
│   │                               SmallChip, LevelProgressBar, LevelMilestoneStrip,
│   │                               EmptyState, KutumbSnackbarHost
│   │
│   ├── viewmodel/
│   │   └── MainViewModel.kt      — Single VM with StateFlow per screen + all actions
│   │
│   └── screens/
│       ├── home/KutumbHomeScreen.kt
│       ├── karya/KaryaScreen.kt
│       ├── niyama/NiyamaScreen.kt
│       ├── vyaya/VyayaScreen.kt
│       ├── rina/RinaScreen.kt
│       ├── samvaad/SamvaadScreen.kt
│       ├── soochi/SoochiScreen.kt
│       ├── smriti/SmritiScreen.kt
│       └── parichay/ParichayScreen.kt
```

## Setup Steps

1. Open `android/` folder in Android Studio (Electric Eel or newer)
2. Sync Gradle
3. Add `res/drawable/kutumb_icon.png` (copy from web `src/imports/Kutumb_icon_512x512.png`)
4. Run on emulator API 26+ or physical device
5. App seeds initial data on first launch via `viewModel.seedIfEmpty()`

## Key Architecture Decisions

| Concern | Choice |
|---------|--------|
| DI | Hilt |
| DB | Room with Flow reactive queries |
| State | `StateFlow` + `collectAsState()` |
| Nav | `NavHost` with single `MainViewModel` |
| Theme | 6 seed × light/dark = 12 modes, persisted in DataStore |
| Score | `combine()` on 8 DB flows → `computeLoyaltyScores()` |

## Adding to an Existing Project

Copy the `java/com/kutumb/app/` folder into your project and update:
- `namespace` in `build.gradle.kts`
- Package declarations at top of each file
- `applicationId` in `defaultConfig`
