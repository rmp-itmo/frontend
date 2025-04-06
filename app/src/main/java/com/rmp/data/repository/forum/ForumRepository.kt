package com.rmp.data.repository.forum

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class PostDto(
    @SerialName("authorId")
    val authorId: Long,
    @SerialName("authorIsMale")
    val authorIsMale: Boolean,
    @SerialName("authorNickname")
    val authorNickname: String,
    @SerialName("id")
    val id: Long,
    @SerialName("image")
    val image: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("title")
    val title: String,
    @SerialName("upVotes")
    val upVotes: Int,
    @SerialName("upvoted")
    val upVoted: Boolean
)

@Serializable
data class FeedDto(
    @SerialName("posts")
    val posts: List<PostDto>
)

@Serializable
data class ProfileDto(
    @SerialName("id")
    val id: Long,
    @SerialName("nickName")
    val nickName: String,
    @SerialName("posts")
    val posts: List<PostDto>,
    @SerialName("registrationDate")
    val registrationDate: Int,
    @SerialName("subsNum")
    val subsNum: Int,
    @SerialName("subscriptions")
    val subscriptions: Map<String, String>,
    @SerialName("isSubscribed")
    val isSubscribed: Boolean,
    @SerialName("isMale")
    val isMale: Boolean,
    @SerialName("name")
    val name: String
)

@Serializable
data class SubscribeDto(
    @SerialName("sub")
    val sub: Boolean,
    @SerialName("targetId")
    val targetId: Long
)

@Serializable
data class UpvoteDto(
    @SerialName("id")
    val id: Long,
    @SerialName("upvote")
    val upvote: Boolean
)

@Serializable
data class CreatePostDto(
    @SerialName("image")
    val image: String? = null,
    @SerialName("imageName")
    val imageName: String? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("title")
    val title: String
)

@Serializable
data class SuccessDto(
    @SerialName("data")
    val `data`: String,
    @SerialName("success")
    val success: Boolean
)

interface ForumRepository {
    suspend fun loadFeed(): FeedDto?

    suspend fun createPostDto(createPostDto: CreatePostDto): PostDto?

    suspend fun loadProfile(userId: Long): ProfileDto?

    suspend fun loadMyProfile(): ProfileDto?

    suspend fun upvotePost(upvoteDto: UpvoteDto): SuccessDto?

    suspend fun subscribe(subscribeDto: SubscribeDto): SuccessDto?
}