package com.interview.kit.di

import com.google.ai.client.generativeai.GenerativeModel
import com.interview.kit.BuildConfig
import com.interview.kit.data.repository.AiRepositoryImpl
import com.interview.kit.domain.repository.AiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    private const val DEFAULT_MODEL = "gemini-1.5-flash"

    @Provides
    @Singleton
    @Named("gemini_api_key")
    fun provideGeminiApiKey(): String {
        // Reads from System property or empty string
        return System.getProperty("GEMINI_API_KEY") ?: ""
    }

    @Provides
    @Singleton
    fun provideGenerativeModel(@Named("gemini_api_key") apiKey: String): GenerativeModel? {
        return if (apiKey.isNotBlank()) {
            GenerativeModel(
                modelName = DEFAULT_MODEL,
                apiKey = apiKey
            )
        } else {
            null
        }
    }

    @Provides
    @Singleton
    fun provideAiRepository(
        generativeModel: GenerativeModel?,
        @Named("gemini_api_key") apiKey: String
    ): AiRepository {
        return AiRepositoryImpl(
            generativeModel = generativeModel,
            isApiKeyValid = apiKey.isNotBlank()
        )
    }
}
