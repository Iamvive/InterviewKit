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
    private val repository: PostRepository = PostRepositoryImpl(apiService)

    @Test
    fun `getPosts maps network DTOs to domain models correctly`() = runTest {
        val dtos = listOf(
            DataPost(
                userId = 1,
                id = 101,
                title = "Interview Title",
                body = "Interview Body"
            ),
            DataPost(
                userId = 2,
                id = 102,
                title = "Second Title",
                body = "Second Body"
            )
        )
        coEvery { apiService.getPosts() } returns dtos

        val domainPosts = repository.getPosts()

        assertEquals(2, domainPosts.size)
        assertEquals(101, domainPosts[0].id)
        assertEquals(1, domainPosts[0].userId)
        assertEquals("Interview Title", domainPosts[0].title)
        assertEquals("Interview Body", domainPosts[0].body)
    }

    @Test(expected = RuntimeException::class)
    fun `getPosts propagates network exceptions`() = runTest {
        coEvery { apiService.getPosts() } throws RuntimeException("Network Error")
        repository.getPosts()
    }
}
