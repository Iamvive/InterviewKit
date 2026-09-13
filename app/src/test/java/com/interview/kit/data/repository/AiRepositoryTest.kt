package com.interview.kit.data.repository

import app.cash.turbine.test
import com.interview.kit.domain.model.Post
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiRepositoryTest {

    @Test
    fun `isLiveConfigured returns false when apiKey is blank or null model`() {
        val repository = AiRepositoryImpl(generativeModel = null, isApiKeyValid = false)
        assertFalse(repository.isLiveConfigured())
    }

    @Test
    fun `generateStream emits chunks in fallback mode`() = runTest {
        val repository = AiRepositoryImpl(generativeModel = null, isApiKeyValid = false)

        repository.generateStream("Explain MVVM").test {
            val firstChunk = awaitItem()
            assertTrue(firstChunk.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `summarizePost triggers stream for post`() = runTest {
        val repository = AiRepositoryImpl(generativeModel = null, isApiKeyValid = false)
        val post = Post(id = 1, userId = 1, title = "Title", body = "Body")

        repository.summarizePost(post).test {
            val chunk = awaitItem()
            assertTrue(chunk.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
