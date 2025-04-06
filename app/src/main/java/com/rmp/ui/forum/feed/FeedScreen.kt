package com.rmp.ui.forum.feed

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rmp.data.UploadedImage
import com.rmp.ui.LocalNavController
import com.rmp.ui.ProfileNavigator
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.PostList
import com.rmp.ui.components.buttons.BackButton
import coil.compose.rememberAsyncImagePainter


@Composable
fun ImagePicker(
    imageSelected: (Uri) -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) imageSelected(uri)
        imageUri = uri
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Pick Image")
        }

        if (imageUri != null)
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUri,
                ),
                contentDescription = "Item image",
                modifier = Modifier.size(70.dp)
            )
    }
}
@Composable
fun CreatePostForm(
    onPostCreate: (String, String?, UploadedImage?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf<String?>(null) }
    var image by remember { mutableStateOf<UploadedImage?>(null) }

    OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Заголовок") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )

    OutlinedTextField(
        value = text ?: "",
        onValueChange = { text = it },
        label = { Text("Текст") },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
    val ctx = LocalContext.current

    ImagePicker { uri ->
        image = UploadedImage.buildFromUri(ctx, uri)
    }

    OutlinedButton(onClick = {
        onPostCreate(title, text, image)
    }) {
        Text("Сохранить")
    }
}

@Composable
fun FeedScreen(
    uiState: FeedUiState,
    fetchFeed: () -> Unit,
    onCreatePost: (String, String?, UploadedImage?) -> Unit,
    onUpvote: (Long, Boolean) -> Unit
) {
    val navigator = LocalNavController.current
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = fetchFeed,
        modifier = Modifier.fillMaxSize()
    ) {
        AppScreen(
            leftComposable = {
                BackButton()
            },
            rightComposable = {
                OutlinedButton(onClick = {
                    navigator.navigate(ProfileNavigator(userId = 0L))
                }) { Text("My profile") }
            }
        ) {
            if (uiState.feed == null) {
                return@AppScreen
            }
            Column {
                CreatePostForm(onCreatePost)
                PostList(
                    posts = uiState.feed.posts,
                    upvoted = onUpvote,
                    authorClicked = {
                        navigator.navigate(ProfileNavigator(userId = it))
                    }
                )
            }
        }
    }
}