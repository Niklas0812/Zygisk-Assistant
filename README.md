# Imposter — Party Word Game

A pass-and-play Android party game inspired by *Splash – Imposter Game*.

Everyone except one player (or a few — your choice) sees the same secret word.
Players take turns saying a one-word clue related to that secret word. The
imposters have to bluff. After the clue round, everyone votes who they think
the imposter is. If they catch an imposter, the crew wins. If not, the
imposters do.

## Features

- 3–16 players on a single device
- 10 built-in categories: Animals, Food, Places, Objects, Sports, Movies,
  Professions, Fruits, Instruments, Tech
- **Random imposter count** — fixed, random within a range, or auto-scaled
  to the total number of players each round
- Hold-to-reveal cards so no one peeks
- Visible clue order so the round starts in the same spot every time
- Running scoreboard across rounds

## How to play

1. Open the app, tap **PLAY**.
2. Add player names (the app starts with 4 placeholder seats).
3. Tap **Settings** to pick categories and choose how many imposters per round
   (Fixed / Random / Auto).
4. Tap **Start Round**. Pass the phone. Each player holds the card to see
   either the **secret word** or the **IMPOSTER** card.
5. Once everyone's seen their role, the app shows the clue order. Going
   around the table, each player says a one-word clue.
6. After clues, tap **Find Imposter** and lock in your group's vote.
7. The result screen reveals the secret word, the imposter(s), and updates the
   scoreboard. Play another round or end the session.

### Scoring

- **Crew wins (caught an imposter):** every crewmate gains **+1 pt**.
- **Imposters win (slipped away):** every imposter gains **+2 pts**.

## Build

Requires Android Studio Hedgehog+ or just the Android SDK + a JDK 17.

```bash
./gradlew assembleDebug
# APK lands in app/build/outputs/apk/debug/app-debug.apk
```

The wrapper pins Gradle 8.7, AGP 8.5.2, Kotlin 1.9.24, Compose BOM 2024.06.

## Project layout

```
app/src/main/java/com/imposter/game/
├── MainActivity.kt          # Compose host + simple screen router
├── data/WordCategories.kt   # All categories + word lists
├── model/Models.kt          # Player, Role, GameSettings, WinResult, GamePhase
├── viewmodel/GameViewModel.kt
└── ui/
    ├── components/          # GradientBackground, PrimaryButton, SecondaryButton
    ├── screens/             # Home, Lobby, Reveal, Clues, Voting, Result, Settings
    └── theme/               # Colors, Theme, Typography
```

## License

MIT.
