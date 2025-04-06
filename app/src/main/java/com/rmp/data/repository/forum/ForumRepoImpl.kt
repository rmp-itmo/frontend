package com.rmp.data.repository.forum

import com.rmp.data.ApiClient
import com.rmp.data.successOr
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class ForumRepoImpl: ForumRepository {
    override suspend fun loadFeed(): FeedDto? {
        return ApiClient.authorizedRequest<FeedDto>(ApiClient.Method.GET, "social/feed").successOr(null)
    }

    override suspend fun createPostDto(createPostDto: CreatePostDto): PostDto? {
        return ApiClient.authorizedRequest<PostDto>(ApiClient.Method.POST, "social/post", createPostDto).successOr(null)
    }

    override suspend fun loadProfile(userId: Long): ProfileDto? {
        return ApiClient.authorizedRequest<ProfileDto>(ApiClient.Method.POST, "social/user/view", buildJsonObject { put("id", userId) }.toString()).successOr(null)
    }

    override suspend fun loadMyProfile(): ProfileDto? {
        return ApiClient.authorizedRequest<ProfileDto>(ApiClient.Method.GET, "social/user").successOr(null)
    }

    override suspend fun upvotePost(upvoteDto: UpvoteDto): SuccessDto? {
        return ApiClient.authorizedRequest<SuccessDto>(ApiClient.Method.PATCH, "social/post/upvote", upvoteDto).successOr(null)
    }

    override suspend fun subscribe(subscribeDto: SubscribeDto): SuccessDto? {
        return ApiClient.authorizedRequest<SuccessDto>(ApiClient.Method.PATCH, "social/user", subscribeDto).successOr(null)
    }
}

fun List<PostDto>.updateAfterUpVoting(postId: Long, upvote: Boolean): List<PostDto> =
    map { post ->
        if (post.id == postId) {
            val newUpVotes = if (upvote) post.upVotes + 1 else post.upVotes - 1
            post.copy(upVoted = upvote, upVotes = newUpVotes)
        } else post
    }