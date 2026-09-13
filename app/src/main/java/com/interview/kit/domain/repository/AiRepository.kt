package com.interview.kit.domain.repository

import com.interview.kit.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    /**
     * Streams AI response tokens in real-time for the given prompt.
     */
    fun generateStream(prompt: String): Flow<String>

    /**
     * Generates a fast AI summary / key takeaways for a post.
     */
    fun summarizePost(post: Post): Flow<String>

    /**
     * Checks if a live Gemini API key is configured.
     */
    fun isLiveConfigured(): Boolean
}
