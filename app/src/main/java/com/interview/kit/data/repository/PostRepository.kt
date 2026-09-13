package com.interview.kit.data.repository

import com.interview.kit.data.api.ApiService
import com.interview.kit.domain.model.Post as DomainPost
import com.interview.kit.data.model.Post as DataPost
import javax.inject.Inject
import javax.inject.Singleton

interface PostRepository {
    suspend fun getPosts(): List<DomainPost>
}

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : PostRepository {
    override suspend fun getPosts(): List<DomainPost> {
        return apiService.getPosts().map { dto ->
            DomainPost(
                id = dto.id,
                userId = dto.userId,
                title = dto.title,
                body = dto.body
            )
        }
    }
}