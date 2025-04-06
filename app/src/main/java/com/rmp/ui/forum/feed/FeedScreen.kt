package com.rmp.ui.forum.feed

import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import android.util.Log

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.rmp.data.UploadedImage
import com.rmp.ui.LocalNavController
import com.rmp.ui.ProfileNavigator
import com.rmp.ui.components.PostList
import com.rmp.ui.components.buttons.BackButton
import com.rmp.R
import com.rmp.ui.components.RefreshedAppScreen
import com.rmp.ui.components.buttons.ProfileButton
import kotlinx.coroutines.delay
import com.rmp.ui.components.AppScreen
import coil.compose.rememberAsyncImagePainter

@Composable
fun FeedScreen(
    uiState: FeedUiState,
    fetchFeed: () -> Unit,
    onCreatePost: (String, String?, UploadedImage?) -> Unit,
    onUpvote: (Long, Boolean) -> Unit
) {
    val navigator = LocalNavController.current
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = uiState.isLoading)

    RefreshedAppScreen(
        leftComposable = { BackButton() },
        rightComposable = { ProfileButton() },
        swipeRefreshState = swipeRefreshState,
        onRefresh = fetchFeed,
    ) {
        if (uiState.feed == null) {
            return@RefreshedAppScreen
        }
        Column {
            var title by remember { mutableStateOf("") }
            var content by remember { mutableStateOf("") }
            var showContent by remember { mutableStateOf(false) }
            var uploadedImage by remember { mutableStateOf<UploadedImage?>(null) }
            var previewBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

            val context = LocalContext.current

            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let {
                    val uploaded = UploadedImage.buildFromUri(context, it)
                    uploadedImage = uploaded

                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    previewBitmap = bitmap.asImageBitmap()
                }
            }
            LaunchedEffect(title) {
                if (title.isNotBlank() && !showContent) {
                    delay(300)
                    showContent = true
                }
                if (title.isBlank() && showContent) {
                    showContent = false
                }
            }

            ElevatedCard (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Row (
                    modifier = Modifier.padding(start = 22.dp, top = 18.dp)
                ) {
                    Text("Поделитесь своим опытом!", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.padding(top = 10.dp, start = 24.dp, bottom = 7.dp)
                ) {
                    Text("Заголовок", fontSize = 14.sp, color = Color.Gray)
                }

                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Введите заголовок...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        shape = MaterialTheme.shapes.medium
                    )
                }


                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier.padding(start = 10.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            placeholder = { Text("Расскажите вашу историю...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            shape = MaterialTheme.shapes.medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column {
                                previewBitmap?.let {
                                    Image(
                                        bitmap = it,
                                        contentDescription = "Превью изображения",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(20.dp))
                                    )
                                }
                            }
                        }

                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button (
                                modifier = Modifier.padding(top = 5.dp),
                                onClick = { imagePickerLauncher.launch("image/*") }
                            ) {
                                Text("Загрузить изображение")
                            }

                            Button(
                                modifier = Modifier.align(Alignment.CenterVertically)
                                    .padding(top = 5.dp),
                                onClick = {
                                    onCreatePost(title, content, uploadedImage)

                                    title = ""
                                    content = ""
                                    previewBitmap = null
                                },
                                enabled = content.isNotBlank()
                            ) {
                                Text("Отправить")
                            }
                        }
                    }
                }
            }

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