# InterviewKit (Base Branch: `main`)

A production-ready starter template for Android technical interviews, live-coding rounds, and rapid prototyping with Clean Architecture and Jetpack Compose.

---

## 🚀 Key Stack & Architecture

- **UI & Theming**: Jetpack Compose, Material 3, Adaptive Layouts, Material Icons Extended
- **Navigation**: Type-Safe Navigation 2.8.2 (`androidx.navigation:navigation-compose`) with `material3-adaptive`
- **Dependency Injection**: Dagger Hilt 2.51.1 (via KSP)
- **Networking & Serialization**: Retrofit 2.11.0, OkHttp 4.12.0, Kotlinx Serialization
- **Image Loading**: Coil Compose
- **State & Concurrency**: Kotlin Coroutines, StateFlow (`collectAsStateWithLifecycle`)
- **Unit Testing Suite**: MockK, Turbine, Kotlinx Coroutines Test, JUnit4
- **Logging**: Timber

---

## 📂 Project Structure

```
app/src/main/java/com/interview/kit/
├── App.kt                           # @HiltAndroidApp application class
├── MainActivity.kt                  # Edge-to-edge Activity with NavigationState
├── di/                              # Dependency Injection modules (Network, Repository)
├── domain/                          # Pure Kotlin domain models
├── data/                            # API service, DTOs, Repository implementations
└── ui/
    ├── home/                        # Home screen & ViewModel
    ├── navigation/                  # Navigation 2.8.x graph and adaptive scaffold
    └── theme/                       # Material 3 Color, Theme, Typography
```

---

## 🧪 Testing

Run JVM unit tests:
```bash
./gradlew testDebugUnitTest
```

The testing suite includes:
- `PostRepositoryTest`: Verifies DTO-to-Domain mapping and error propagation with MockK.
- `HomeViewModelTest`: Verifies initial loading, success, retry, and error flows with Turbine and `StandardTestDispatcher`.

---

## 🌿 Branches in this Repository

- **`main`**: Standard / Base setup (Zero AI dependencies — ideal for FAANG and strict coding rounds).
- **`ai-powers`**: AI-augmented setup with Google Generative AI (Gemini SDK), `AiRepository`, and streaming Compose UI.
