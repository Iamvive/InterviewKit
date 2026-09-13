# Interview Starter Multi-Branch Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create two distinct, fully tested starter branches (`main` for strict non-AI coding rounds and `ai-powers` for AI-augmented rounds).

**Architecture:** 
- `main`: Clean Architecture + MVVM baseline with Hilt, Retrofit, Kotlinx Serialization, Navigation 3, Compose Material 3, and pre-wired unit tests (MockK, Turbine, Coroutines Test Dispatcher).
- `ai-powers`: Built on `main`, integrating the Google Generative AI (Gemini) SDK, an `AiRepository` for streaming and summarization, and an interactive AI Assistant Compose bottom sheet.

**Tech Stack:** Kotlin 2.0.20, Jetpack Compose Material 3, Navigation 3, Dagger Hilt 2.51.1, Retrofit 2.11.0, Kotlinx Serialization, MockK 1.13.12, Turbine 1.1.0, Google Generative AI SDK 0.9.0.

## Global Constraints
- Keep Android SDK targets intact (`compileSdk = 36`, `minSdk = 24`, `targetSdk = 36`).
- All tests must be fast local JVM unit tests (`./gradlew testDebugUnitTest`).
- Preserve existing package structures under `com.interview.kit`.
- Commit after each task.

---

### Task 1: Complete Base Testing Infrastructure & Fix Theme Warning on `main`

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Modify: `app/src/main/java/com/interview/kit/ui/theme/Theme.kt:70-85`
- Create: `app/src/test/java/com/interview/kit/data/repository/PostRepositoryTest.kt`
- Create: `app/src/test/java/com/interview/kit/ui/home/HomeViewModelTest.kt`
- Modify: `README.md`

**Interfaces:**
- Consumes: `PostRepository`, `ApiService`, `HomeViewModel`, `HomeUiState`
- Produces: Complete unit test coverage for `main` branch with MockK and Turbine

- [ ] **Step 1: Add unit test dependencies to version catalog**

Edit `gradle/libs.versions.toml` to add:
```toml
mockk = "1.13.12"
turbine = "1.1.0"
kotlinxCoroutinesTest = "1.8.1"

# libraries
mockk = { group = "io.mockk", name = "mockk", version.ref = "mockk" }
turbine = { group = "app.cash.turbine", name = "turbine", version.ref = "turbine" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "kotlinxCoroutinesTest" }
```

- [ ] **Step 2: Add test dependencies to `app/build.gradle.kts`**

Add to `dependencies`:
```kotlin
testImplementation(libs.mockk)
testImplementation(libs.turbine)
testImplementation(libs.kotlinx.coroutines.test)
```

- [ ] **Step 3: Clean up deprecated statusBarColor in `Theme.kt`**

Replace the deprecated `window.statusBarColor` code block with `enableEdgeToEdge()` compatible theming.

- [ ] **Step 4: Write `PostRepositoryTest.kt`**

```kotlin
package com.interview.kit.data.repository

import com.interview.kit.data.api.ApiService
import com.interview.kit.data.model.Post as DataPost
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PostRepositoryTest {
    private val apiService: ApiService = mockk()
    private val repository = PostRepositoryImpl(apiService)

    @Test
    fun `getPosts maps network DTOs to domain models correctly`() = runTest {
        val dtos = listOf(
            DataPost(userId = 1, id = 101, title = "Test Title", body = "Test Body")
        )
        coEvery { apiService.getPosts() } returns dtos

        val result = repository.getPosts()

        assertEquals(1, result.size)
        assertEquals(101, result[0].id)
        assertEquals("Test Title", result[0].title)
    }
}
```

- [ ] **Step 5: Write `HomeViewModelTest.kt`**

```kotlin
package com.interview.kit.ui.home

import app.cash.turbine.test
import com.interview.kit.data.repository.PostRepository
import com.interview.kit.domain.model.Post
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val repository: PostRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial fetch emits Loading and then Success on repository success`() = runTest {
        val samplePosts = listOf(Post(id = 1, userId = 1, title = "A", body = "B"))
        coEvery { repository.getPosts() } returns samplePosts

        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is HomeUiState.Success)
            assertEquals(samplePosts, (state as HomeUiState.Success).posts)
        }
    }

    @Test
    fun `initial fetch emits Loading and then Error on repository exception`() = runTest {
        coEvery { repository.getPosts() } throws RuntimeException("Network timeout")

        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is HomeUiState.Error)
            assertEquals("Network timeout", (state as HomeUiState.Error).message)
        }
    }
}
```

- [ ] **Step 6: Update `README.md` for `main`**

Update `README.md` with full interview guidelines and instructions.

- [ ] **Step 7: Run unit tests to verify**

Run: `./gradlew testDebugUnitTest`  
Expected: All unit tests PASS.

- [ ] **Step 8: Commit `main` branch setup**

```bash
git add .
git commit -m "feat(base): configure test dependencies, unit tests, and clean up Theme"
```

---

### Task 2: Create `ai-powers` Branch & Integrate Gemini SDK

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `app/build.gradle.kts`
- Create: `app/src/main/java/com/interview/kit/di/AiModule.kt`
- Create: `app/src/main/java/com/interview/kit/domain/repository/AiRepository.kt`
- Create: `app/src/main/java/com/interview/kit/data/repository/AiRepositoryImpl.kt`

**Interfaces:**
- Consumes: Google Generative AI SDK, Hilt SingletonComponent
- Produces: Injected `AiRepository` supporting streaming and summarization

- [ ] **Step 1: Create and switch to branch `ai-powers`**

```bash
git checkout -b ai-powers
```

- [ ] **Step 2: Add Generative AI SDK to `libs.versions.toml` and `app/build.gradle.kts`**

In `libs.versions.toml`:
```toml
generativeai = "0.9.0"
google-ai-generativeai = { group = "com.google.ai.client.generativeai", name = "generativeai", version.ref = "generativeai" }
```

In `app/build.gradle.kts`:
```kotlin
implementation(libs.google.ai.generativeai)
```

- [ ] **Step 3: Create `AiModule.kt`**

Provide `GenerativeModel` with API key fallback (fallback to mock mode if no API key is set so the app never crashes during an interview).

- [ ] **Step 4: Create `AiRepository.kt` and `AiRepositoryImpl.kt`**

Define interface and implementation with real streaming support (`generateStream(prompt)` and `summarizePost(post)`).

- [ ] **Step 5: Write unit tests for `AiRepositoryTest.kt`**

Verify streaming flows and fallback handling.

- [ ] **Step 6: Run unit tests**

Run: `./gradlew testDebugUnitTest`  
Expected: PASS

- [ ] **Step 7: Commit AI data layer**

```bash
git add .
git commit -m "feat(ai): integrate Google Generative AI SDK and create AiRepository"
```

---

### Task 3: Build AI Assistant UI & Demo in `ai-powers`

**Files:**
- Create: `app/src/main/java/com/interview/kit/ui/ai/AiViewModel.kt`
- Create: `app/src/main/java/com/interview/kit/ui/ai/AiAssistantSheet.kt`
- Modify: `app/src/main/java/com/interview/kit/ui/home/HomeScreen.kt`
- Modify: `README.md` (on `ai-powers` branch)

**Interfaces:**
- Consumes: `AiRepository`, Compose Material 3 ModalBottomSheet
- Produces: Interactive AI Assistant UI with token streaming on `HomeScreen`

- [ ] **Step 1: Create `AiViewModel.kt`**

State holder managing `AiUiState` (Idle, Streaming(text), Done(text), Error(message)).

- [ ] **Step 2: Create `AiAssistantSheet.kt`**

Composable bottom sheet displaying streaming response with quick action chips ("✨ Summarize List", "💡 Key Insights").

- [ ] **Step 3: Integrate floating AI trigger button in `HomeScreen.kt`**

Add an AI floating action button or top bar action that opens `AiAssistantSheet`.

- [ ] **Step 4: Update `README.md` on `ai-powers` branch**

Explain how to set `GEMINI_API_KEY` and how to demonstrate streaming AI in interviews.

- [ ] **Step 5: Run tests and assemble debug APK**

Run: `./gradlew testDebugUnitTest assembleDebug`  
Expected: All tests pass and APK builds cleanly.

- [ ] **Step 6: Commit AI UI & documentation**

```bash
git add .
git commit -m "feat(ai): add AI Assistant bottom sheet and HomeScreen integration"
```

---

### Task 4: Final Verification & Branch Sync

- [ ] **Step 1: Verify `ai-powers` branch builds cleanly**
- [ ] **Step 2: Switch to `main` branch and verify `main` remains pristine**
- [ ] **Step 3: Summary documentation for switching between branches**
