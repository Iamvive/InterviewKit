# Interview Starter Multi-Branch Design Specification

**Date**: 2026-09-13  
**Status**: Approved for Implementation

---

## 1. Overview & Objective

Provide a modular, ready-to-clone Android starter template for technical interview coding rounds, supporting two distinct branching profiles:
1. **`main` (Base / Non-AI Setup)**: Strict, production-grade Android Clean Architecture starter with Jetpack Compose, Material 3, Navigation 3, Hilt, Retrofit, and complete pre-configured Unit Testing infrastructure (MockK, Coroutines Test, Turbine).
2. **`ai-powers` (AI-Enabled Setup)**: Modern starter extending `main` with Google Generative AI (Gemini) SDK, Hilt-injected `AiRepository`, real-time token streaming, and an AI assistant Compose UI component.

---

## 2. Branch Architecture & Specifications

### 2.1 Branch `main` (Standard Android Starter)

#### Dependencies to Add ([`libs.versions.toml`](file:///Users/appworx/Desktop/Velo/gradle/libs.versions.toml)):
- `mockk` (`io.mockk:mockk:1.13.12`)
- `kotlinx-coroutines-test` (`org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1`)
- `turbine` (`app.cash.turbine:turbine:1.1.0`)

#### Testing Infrastructure:
- **`HomeViewModelTest.kt`**:
  - Test initial loading state
  - Test successful posts fetch updates `HomeUiState.Success`
  - Test repository error maps to `HomeUiState.Error`
  - Uses `StandardTestDispatcher` / `TestScope` and Turbine's `uiState.test { ... }`
- **`PostRepositoryTest.kt`**:
  - Mock `ApiService` using `MockK`
  - Verify DTO to Domain model mapping
  - Verify exception propagation

#### Maintenance:
- Remove deprecated `statusBarColor` invocation in `Theme.kt`.
- Update `README.md` with standard interview execution tips.

---

### 2.2 Branch `ai-powers` (AI-Augmented Starter)

#### Dependencies to Add:
- `generativeai` (`com.google.ai.client.generativeai:generativeai:0.9.0`)

#### AI Architecture Components:
- **DI Module (`di/AiModule.kt`)**:
  - Provides `GenerativeModel` (e.g. `gemini-1.5-flash`)
  - Supports configurable API key via `BuildConfig` with graceful fallback for offline/mock mode
- **Domain Layer (`domain/repository/AiRepository.kt`)**:
  - `fun generateStream(prompt: String): Flow<String>`
  - `fun summarizePost(post: Post): Flow<String>`
- **Data Layer (`data/repository/AiRepositoryImpl.kt`)**:
  - Implements `AiRepository` invoking Gemini client's `generateContentStream`
- **Presentation Layer**:
  - `AiAssistantBottomSheet.kt`: Interactive bottom sheet supporting prompt inputs, action chips ("Summarize", "Key Insights"), and streaming markdown/text response.
  - Integration on `HomeScreen` or detail view.
- **Documentation**:
  - `README.md` on `ai-powers` branch detailing Gemini API setup and quick interview prompts.

---

## 3. Verification Plan

1. **`main` Branch**:
   - Run `./gradlew testDebugUnitTest` -> All unit tests pass.
   - Run `./gradlew assembleDebug` -> Clean build without warnings/errors.
2. **`ai-powers` Branch**:
   - Run `./gradlew testDebugUnitTest` -> All tests pass.
   - Run `./gradlew assembleDebug` -> Builds cleanly with Gemini SDK and AI UI.
