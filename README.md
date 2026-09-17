# QuizRush – Kids & Adults Quiz

**QuizRush** is an offline-first trivia and quiz application for Android built with Kotlin and Jetpack Compose, featuring Google AdMob monetization.

---

## 🎯 Core Features & Gameplay

### 1. Dual Mode System
- **👦 Kids Mode:** Cheerful, vibrant aesthetic with 178 questions covering basic mathematics, shapes, animals & birds, world landmarks, countries, Indian states/capitals, simple science, and geography.
- **🧑 Adults Mode:** Sleek, high-contrast cerebral aesthetic with 179 questions covering percentages, ratios, algebra, logical reasoning, Indian history & polity, space & modern technology, and world geography.

### 2. Gameplay Mechanics
- **Balanced Pool:** Each difficulty level (**Easy, Medium, Hard**) draws from substantial local question pools.
- **Randomized Quizzes:** Every quiz selects **exactly 10 questions randomly** from the appropriate section/difficulty pool with no repeats within the same quiz.
- **Randomized Options:** 4 multiple-choice options with positions shuffled on every attempt.
- **60-Second Timer:** Dynamic color-changing countdown timer (Green > Amber > Pulsing Crimson alert).
- **3-Lives Heart System:** 3 lives per attempt (`❤️❤️❤️`). Correct answers preserve lives; wrong answers or timeouts deduct 1 life.
- **Single Rewarded Revival:** Option to watch 1 rewarded sponsor ad per quiz to restore 1 life when hearts reach 0.
- **Streak & Time Bonuses:** Multipliers for streaks (1.25x for 3+, 1.5x for 5+) and speed bonuses for quick thinking.
- **Comprehensive Review:** Full review screen for all 10 attempted questions with user choice, correct answer, and clear educational explanations.

### 3. 100% Offline Architecture
- **357 Questions** stored locally in `app/src/main/assets/questions_kids.json` and `questions_adults.json`.
- Zero external APIs, cloud databases, user logins, or subscriptions required.
- Statistics, achievements, high scores, and settings persist securely using local `SharedPreferences`.

### 4. Google AdMob Monetization
- **Banner Ads:** Displayed at the bottom of the Home screen and Results screen.
- **Interstitial Ads:** Triggered upon quiz completion (never during an active question).
- **Rewarded Video Ads:** Offered exclusively for a single +1 extra life restoration.
- **Offline Fallback:** If internet is disconnected, the app continues seamlessly with simulated offline reward lifelines.

---

## 🏗️ Architecture & Code Organization

```
app/src/main/java/com/example/
├── MainActivity.kt                # Application entry point & edge-to-edge container
├── data/
│   ├── model/QuizModels.kt        # Data classes: Question, QuizQuestionState, QuizResult, UserStats, Achievement
│   ├── repository/QuizRepository.kt # Asset JSON parser, random question selector, and daily challenge generator
│   └── local/QuizPreferences.kt   # Offline SharedPreferences persistence for stats and settings
├── service/
│   ├── AdMobManager.kt            # Google Mobile Ads integration (Banner, Interstitial, Rewarded)
│   └── AudioFeedback.kt           # ToneGenerator arcade sound synthesizer and haptic vibration engine
├── ui/
│   ├── components/
│   │   ├── AdMobBanner.kt         # Compose AndroidView wrapper for AdMob Banner
│   │   ├── ConfettiEffect.kt      # Interactive particle burst celebration canvas
│   │   ├── LivesIndicator.kt      # Pulsing heart life counter and revival badge
│   │   └── TimerIndicator.kt      # 60-second circular timer with state-driven color transitions
│   ├── screens/
│   │   ├── SplashScreen.kt        # Branded launch screen
│   │   ├── HomeScreen.kt          # Kids/Adults mode switcher, difficulty cards, and daily challenge
│   │   ├── QuizScreen.kt          # 10 questions, 60s timer, 3 lives, options, pause & revival dialogs
│   │   ├── ResultsScreen.kt       # Score summary, accuracy gauge, XP, streak, confetti, and share intent
│   │   ├── ReviewScreen.kt        # Question-by-question review with explanations
│   │   ├── StatisticsScreen.kt    # Lifetime stats, accuracy, and high score records
│   │   ├── AchievementsScreen.kt  # 8 tiered achievements with real-time progress bars
│   │   └── SettingsScreen.kt      # Sound, haptics, theme (System/Light/Dark), and AdMob test mode
│   └── theme/
│       ├── Color.kt               # Kids (Warm sunset) & Adults (Midnight indigo) color schemes
│       ├── Theme.kt               # Material 3 dynamic theme provider
│       └── Type.kt                # Typography styles
```

---

## 🧪 Testing & Verification

- **Robolectric Local JVM Tests:** Validates question bank completeness, 10-question quiz generation with shuffled answers, scoring rules, and preference storage.
- **Screenshot Verification:** Roborazzi automated visual tests for layout fidelity.
- **Command:** `gradle :app:testDebugUnitTest`
