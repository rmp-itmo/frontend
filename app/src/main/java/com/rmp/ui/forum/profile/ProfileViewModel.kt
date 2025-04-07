package com.rmp.ui.forum.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.ErrorMessage
import com.rmp.data.repository.forum.ForumRepository
import com.rmp.data.repository.forum.ProfileDto
import com.rmp.data.repository.forum.SubscribeDto
import com.rmp.data.repository.forum.UpvoteDto
import com.rmp.data.repository.forum.updateAfterUpVoting
import com.rmp.ui.forum.feed.FeedViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isMe: Boolean = false,
    val profile: ProfileDto? = null,
    val isLoading: Boolean = false,
    val errors: List<ErrorMessage> = emptyList()
)

class ProfileViewModel(
    private val userId: Long,
    private val forumRepository: ForumRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState(userId == 0L))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy()
            }
            fetchProfile()
        }
    }

    companion object {
        fun factory(userId: Long, forumRepository: ForumRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(userId, forumRepository) as T
            }
        }
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val isMe = _uiState.value.isMe

            val profile = async {
                if (isMe) forumRepository.loadMyProfile()
                else forumRepository.loadProfile(userId)
            }.await() ?: run {
                _uiState.update {
                    it.copy(isLoading = false, errors = listOf(ErrorMessage(null, R.string.error_load_data)))
                }
                return@launch
            }

            _uiState.update {
                it.copy(isLoading = false, profile = profile, errors = emptyList())
            }
        }
    }

    fun subscribe() {
        viewModelScope.launch {
            if (_uiState.value.isMe) return@launch

            _uiState.update {
                it.copy(isLoading = true)
            }

            val sub = !(_uiState.value.profile?.isSubscribed ?: false)

            async { forumRepository.subscribe(SubscribeDto(targetId = userId, sub = sub)) }.await() ?: run {
                _uiState.update {
                    it.copy(isLoading = false, errors = listOf(ErrorMessage(null, R.string.error_load_data)))
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    profile = it.profile?.copy(
                        subsNum =
                            if (sub) it.profile.subsNum + 1
                            else it.profile.subsNum - 1,
                        isSubscribed = sub
                    ),
                    errors = emptyList()
                )
            }
        }
    }

    fun upvotePost(postId: Long, upvote: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            async { forumRepository.upvotePost(UpvoteDto(id = postId, upvote = upvote)) }.await()
                ?: run {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errors = listOf(ErrorMessage(null, R.string.error_load_data))
                        )
                    }
                    return@launch
                }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    errors = emptyList(),
                    profile = it.profile?.copy(posts = it.profile.posts.updateAfterUpVoting(postId, upvote))
                )
            }
        }
    }
}