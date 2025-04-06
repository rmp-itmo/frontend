package com.rmp.ui.forum.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.ErrorMessage
import com.rmp.data.UploadedImage
import com.rmp.data.repository.forum.CreatePostDto
import com.rmp.data.repository.forum.FeedDto
import com.rmp.data.repository.forum.ForumRepository
import com.rmp.data.repository.forum.PostDto
import com.rmp.data.repository.forum.UpvoteDto
import com.rmp.data.repository.forum.updateAfterUpVoting
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedUiState(
    val isLoading: Boolean = false,
    val feed: FeedDto? = null,
    val errors: List<ErrorMessage> = emptyList()
)

fun FeedDto.prependPost(postDto: PostDto): FeedDto =
    FeedDto(posts = posts + postDto)

class FeedViewModel(
    private val forumRepository: ForumRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState(isLoading = true))
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fetchFeed()
        }
    }

    companion object {
        fun factory(forumRepository: ForumRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FeedViewModel(forumRepository) as T
            }
        }
    }

    fun fetchFeed() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val feedData = async { forumRepository.loadFeed() }.await() ?: run {
                _uiState.update {
                    it.copy(isLoading = false, errors = listOf(ErrorMessage(null, R.string.error_load_data)))
                }
                return@launch
            }

            _uiState.update {
                it.copy(isLoading = false, feed = feedData, errors = emptyList())
            }

        }
    }

    fun createPost(title: String, text: String? = null, image: UploadedImage? = null) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val postData = async { forumRepository.createPostDto(CreatePostDto(
                title = title,
                image = image?.image,
                imageName = image?.imageName,
                text = text
            )) }.await() ?: run {
                //TODO: Change error to "Failed to upload"
                _uiState.update {
                    it.copy(isLoading = false, errors = listOf(ErrorMessage(null, R.string.error_load_data)))
                }
                return@launch
            }

            _uiState.update {
                it.copy(isLoading = false, errors = emptyList(), feed = it.feed?.prependPost(postData))
            }
        }
    }

    fun upvotePost(postId: Long, upvote: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            async { forumRepository.upvotePost(UpvoteDto(id = postId, upvote = upvote)) }.await() ?: run {
                //TODO: Change error to "Failed to upload"
                _uiState.update {
                    it.copy(isLoading = false, errors = listOf(ErrorMessage(null, R.string.error_load_data)))
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    errors = emptyList(),
                    feed = it.feed?.copy(posts = it.feed.posts.updateAfterUpVoting(postId, upvote))
                )
            }
        }
    }
}