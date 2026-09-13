package com.interview.kit.ui.ai

import app.cash.turbine.test
import com.interview.kit.domain.model.Post
import com.interview.kit.domain.repository.AiRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
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
class AiViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val aiRepository: AiRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Idle`() {
        every { aiRepository.isLiveConfigured() } returns false
        val viewModel = AiViewModel(aiRepository)
        assertEquals(AiUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `sendPrompt accumulates streaming chunks and finishes with Done`() = runTest {
        every { aiRepository.isLiveConfigured() } returns true
        every { aiRepository.generateStream("test prompt") } returns flowOf("Hello", " ", "World")

        val viewModel = AiViewModel(aiRepository)

        viewModel.uiState.test {
            assertEquals(AiUiState.Idle, awaitItem())

            viewModel.sendPrompt("test prompt")
            assertEquals(AiUiState.Streaming(""), awaitItem())

            testDispatcher.scheduler.advanceUntilIdle()

            // Read the intermediate streaming states
            var current = awaitItem()
            while (current !is AiUiState.Done) {
                assertTrue(current is AiUiState.Streaming)
                current = awaitItem()
            }

            assertEquals(AiUiState.Done("Hello World"), current)
        }
    }

    @Test
    fun `sendPrompt handles repository stream error`() = runTest {
        every { aiRepository.isLiveConfigured() } returns true
        every { aiRepository.generateStream(any()) } returns flow {
            throw RuntimeException("API Rate limit exceeded")
        }

        val viewModel = AiViewModel(aiRepository)

        viewModel.uiState.test {
            assertEquals(AiUiState.Idle, awaitItem())

            viewModel.sendPrompt("prompt")
            assertEquals(AiUiState.Streaming(""), awaitItem())

            testDispatcher.scheduler.advanceUntilIdle()
            val errorState = awaitItem()
            assertTrue(errorState is AiUiState.Error)
            assertEquals("API Rate limit exceeded", (errorState as AiUiState.Error).message)
        }
    }

    @Test
    fun `reset returns state to Idle`() = runTest {
        every { aiRepository.isLiveConfigured() } returns false
        every { aiRepository.generateStream(any()) } returns flowOf("Chunk")

        val viewModel = AiViewModel(aiRepository)
        viewModel.sendPrompt("prompt")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.reset()
        assertEquals(AiUiState.Idle, viewModel.uiState.value)
    }
}
