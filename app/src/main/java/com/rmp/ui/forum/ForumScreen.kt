package com.rmp.ui.forum

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rmp.R


@Composable
fun NewPostCardWrapper() {
    var isExpanded by remember { mutableStateOf(false) }

    BoxWithClickOutsideHandler(
        isExpanded = isExpanded,
        onClickOutside = { isExpanded = false }
    ) {
        NewPostCard(
            isExpanded = isExpanded,
            setExpanded = { isExpanded = it }
        )
    }
}

@Composable
fun BoxWithClickOutsideHandler(
    isExpanded: Boolean,
    onClickOutside: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isExpanded) {
                if (isExpanded) {
                    detectTapGestures(onTap = {
                        onClickOutside()
                    })
                }
            }
    ) {
        content()
    }
}
@Composable
fun NewPostCard(
    isExpanded: Boolean,
    setExpanded: (Boolean) -> Unit
) {
    var title by remember { mutableStateOf(TextFieldValue()) }
    var text by remember { mutableStateOf(TextFieldValue()) }
    var mediaUri by remember { mutableStateOf<Uri?>(null) }
    var isTitleError by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        mediaUri = uri
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 21.dp, vertical = 30.dp)
            .clickable { setExpanded(true) },
        shape = RoundedCornerShape(20.dp),
        elevation = cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {}) // блокирует сворачивание при клике внутри карточки
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isExpanded) {
                Text(
                    text = "Поделись своим опытом!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 21.dp, vertical = 10.dp)
                )
            }

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    isTitleError = it.text.isBlank()
                    setExpanded(true)
                },
                label = { Text(stringResource(R.string.post_title)) },
                placeholder = { Text(stringResource(R.string.post_title_input)) },
                isError = isTitleError,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Gray,
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray,
                    errorBorderColor = Color.Red
                )
            )

            if (isTitleError) {
                Text("Заголовок обязателен", color = Color.Red, fontSize = 12.sp)
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(stringResource(R.string.post_text)) },
                    placeholder = { Text(stringResource(R.string.post_text_input)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Gray,
                        focusedBorderColor = Color.Black,
                        unfocusedBorderColor = Color.Gray,
                        errorBorderColor = Color.Red
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mediaUri?.lastPathSegment ?: "",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text(stringResource(R.string.post_img)) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.upload),
                            contentDescription = "Upload icon",
                            modifier = Modifier
                                .clickable { launcher.launch("image/*") }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { launcher.launch("image/*") },
                    shape = RoundedCornerShape(15.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Gray,
                        disabledBorderColor = Color.LightGray,
                        disabledLabelColor = Color.Gray,
                        disabledPlaceholderColor = Color.Gray
                    )
                )

                mediaUri?.let { uri ->
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = uri,
                        contentDescription = "Предпросмотр изображения",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val isSendEnabled = title.text.isNotBlank()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(
                            if (isSendEnabled)
                                Brush.horizontalGradient(listOf(Color(0xFFF9881F), Color(0xFFFF774C)))
                            else
                                Brush.horizontalGradient(listOf(Color.LightGray, Color.Gray))
                        )
                        .clickable(enabled = isSendEnabled) {
                            if (title.text.isBlank()) {
                                isTitleError = true
                                return@clickable
                            }
                            // TODO: обработка отправки
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Отправить",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            }
        }
    }
}

data class PostData(
    val id: Int,
    val authorId: Int,
    val authorNickname: String,
    val authorIsMale: Boolean,
    val upVotes: Int,
    val image: String?,
    val text: String,
    val title: String
)


@Composable
fun PostCard(post: PostData) {
    var upVotes by remember { mutableStateOf(post.upVotes) }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val avatarRes = if (post.authorIsMale) R.drawable.male_avatar else R.drawable.female_avatar
                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = "Аватар",
                    modifier = Modifier
                        .size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(post.authorNickname, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = post.title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(text = post.text, fontSize = 14.sp, color = Color.DarkGray)

            post.image?.let {
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = it,
                    contentDescription = "Изображение поста",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Upvote",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            upVotes += 1 // заменить API вызовом
                        }
                )
                Text("$upVotes", modifier = Modifier.padding(horizontal = 8.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Downvote",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            upVotes = maxOf(0, upVotes - 1) // заменить API вызовом
                        }
                )
            }
        }
    }
}