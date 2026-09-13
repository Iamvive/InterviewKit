package com.interview.kit.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.interview.kit.domain.model.Post
import com.interview.kit.domain.repository.AiRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val generativeModel: GenerativeModel?,
    private val isApiKeyValid: Boolean
) : AiRepository {

    override fun isLiveConfigured(): Boolean = isApiKeyValid && generativeModel != null

    override fun generateStream(prompt: String): Flow<String> {
        val model = generativeModel
        if (isLiveConfigured() && model != null) {
            return model.generateContentStream(prompt).map { response ->
                response.text ?: ""
            }
        }

        // Offline / Demo fallback stream (for seamless interview demonstration without API key setup)
        return flow {
            val demoResponse = "✨ [AI Assistant]: Analyzing your request...\n\n" +
                    "Here is the key summary for \"$prompt\":\n" +
                    "• High architectural cohesion across domain models.\n" +
                    "• Low latency token streaming in Jetpack Compose.\n" +
                    "• Ready for live API substitution with Gemini 1.5 Flash."
            
            for (chunk in demoResponse.split(" ")) {
                emit("$chunk ")
                delay(35)
            }
        }
    }

    override fun summarizePost(post: Post): Flow<String> {
        val prompt = "Summarize the following post in 2 concise bullet points:\n\n" +
                "Title: ${post.title}\n" +
                "Content: ${post.body}"
        return generateStream(prompt)
    }
}
