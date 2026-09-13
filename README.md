# InterviewKit (Branch: `ai-powers`)

An AI-augmented Android starter template featuring the official **Google Generative AI (Gemini) SDK**, Clean Architecture, Jetpack Compose, and real-time token streaming.

---

## ⚡ AI Features Included

1. **Google Generative AI Client**: Pre-configured with `gemini-1.5-flash` model.
2. **Streaming Architecture**: Real-time token streaming (`Flow<String>`) in `AiRepository`.
3. **AI Copilot Bottom Sheet**: Interactive Material 3 bottom sheet with quick action prompts ("Compose UDF Architecture", "Testing with Turbine", "Clean Architecture Tips") and live token-by-token rendering.
4. **Dual Mode Execution**:
   - **⚡ Live Gemini**: Enabled when `GEMINI_API_KEY` is provided.
   - **✨ Demo Stream**: Works seamlessly out of the box without any API key (ideal for live interviews if network or API keys are restricted).

---

## 🔑 Setting Up Your Gemini API Key (Optional)

You can pass your free Gemini API key via JVM system property or gradle property:

```bash
# In local.properties or command line
./gradlew installDebug -DGEMINI_API_KEY="your-api-key-here"
```

Or inject it directly in [`AiModule.kt`](file:///Users/appworx/Desktop/Velo/app/src/main/java/com/interview/kit/di/AiModule.kt).

---

## 📂 Architecture

```
app/src/main/java/com/interview/kit/
├── di/
│   ├── AiModule.kt                  # Injects GenerativeModel & AiRepository
│   ├── NetworkModule.kt             # Retrofit + OkHttp
│   └── RepositoryModule.kt          # PostRepository binding
├── domain/repository/
│   └── AiRepository.kt              # Interface for AI streaming & summarization
├── data/repository/
│   └── AiRepositoryImpl.kt          # Implements Gemini streaming with demo fallback
└── ui/ai/
    ├── AiViewModel.kt               # Manages AiUiState (Idle, Streaming, Done, Error)
    └── AiAssistantSheet.kt          # Composable bottom sheet with streaming UI
```

---

## 🧪 Testing

Run all unit tests (Repository, ViewModel, AI streaming flows):
```bash
./gradlew testDebugUnitTest
```

---

## 🌿 Switching Branches

- To use the **Strict / Non-AI base template**:
  ```bash
  git checkout main
  ```
- To return to the **AI-enabled template**:
  ```bash
  git checkout ai-powers
  ```
