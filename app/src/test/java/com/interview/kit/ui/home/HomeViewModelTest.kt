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
        val samplePosts = listOf(
            Post(id = 1, userId = 1, title = "Post 1", body = "Body 1"),
            Post(id = 2, userId = 1, title = "Post 2", body = "Body 2")
        )
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
    fun `initial fetch emits Loading and then Error on repository failure`() = runTest {
        coEvery { repository.getPosts() } throws RuntimeException("Unable to connect")

        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertTrue(state is HomeUiState.Error)
            assertEquals("Unable to connect", (state as HomeUiState.Error).message)
        }
    }

    @Test
    fun `fetchPosts can be retried successfully after error`() = runTest {
        coEvery { repository.getPosts() } throws RuntimeException("Network error") andThen listOf(
            Post(id = 1, userId = 1, title = "Success", body = "Recovered")
        )

        val viewModel = HomeViewModel(repository)

        viewModel.uiState.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val errorState = awaitItem()
            assertTrue(errorState is HomeUiState.Error)

            // Trigger retry
            viewModel.fetchPosts()
            assertEquals(HomeUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            val successState = awaitItem()
            assertTrue(successState is HomeUiState.Success)
            assertEquals(1, (successState as HomeUiState.Success).posts.size)
        }
    }
}
