package com.rmp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import com.rmp.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil.compose.rememberAsyncImagePainter
import com.rmp.data.baseUrl
import com.rmp.data.repository.forum.PostDto

@Composable
fun PostList(
    posts: List<PostDto>,
    upvoted: (Long, Boolean) -> Unit,
    authorClicked: (Long) -> Unit,
) {
    LazyVerticalGrid(
        GridCells.Fixed(1),
        modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.Center
    ) {
        items(posts) { post ->
            PostCard(post = post, authorClicked, upvoted)
        }
    }
}

@Composable
fun PostCard(
    post: PostDto,
    authorClicked: (Long) -> Unit,
    upvoted: (Long, Boolean) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
    ) {


        Row(
            modifier = Modifier
                .padding(start = 22.dp, top = 18.dp)
                .clickable { authorClicked(post.authorId) }
        ) {
            Icon(
                painter = painterResource(
                    if (post.authorIsMale) {
                        R.drawable.man
                    } else {
                        R.drawable.women
                    }
                ),
                contentDescription = (stringResource(R.string.menu)),
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.CenterVertically)
            )

            Text(
                "@${post.authorNickname}",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 15.dp)
            )
        }

        Row (
            modifier = Modifier.padding(start = 15.dp, top = 15.dp)
        ) {
            Text(
                post.title,
                fontWeight = FontWeight.Bold
            )
        }
        Row (
            modifier = Modifier.padding(start = 15.dp, top = 15.dp)
        ) {
            Text(post.text ?: "")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (post.image != null) {
                DynamicImage(post)
            }
        }
        Row (
            modifier = Modifier.padding(start = 15.dp, top = 15.dp, bottom = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)

        ) {
            Row(
                modifier = Modifier
                    .border(1.dp, Color.Black, RoundedCornerShape(5.dp))
                    .clickable { upvoted(post.id, true) }
                    .clip(RoundedCornerShape(5.dp)),

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.up),
                        contentDescription = stringResource(R.string.menu),
                        modifier = Modifier.size(35.dp)
                    )

                    Text(
                        "${post.upVotes}",
                        modifier = Modifier.padding(end = 10.dp)
                        )
                }
            }

            Icon(
                painter = painterResource(R.drawable.down),
                contentDescription = (stringResource(R.string.menu)),
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.CenterVertically)
                    .clickable { upvoted(post.id, false) }
            )
        }
    }
}
@Composable
fun DynamicImage(post: PostDto) {
    if (post.image != null) {
        SubcomposeAsyncImage(
            model = "$baseUrl/files/${post.image}",
            contentDescription = "Post image",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop,
            loading = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            },
            success = { state ->
                val intrinsicSize = state.painter.intrinsicSize
                val aspectRatio = intrinsicSize.width / intrinsicSize.height

                Box(
                    modifier = Modifier
                        .padding(top = 15.dp, start = 15.dp, end = 15.dp)
                        .fillMaxWidth()
                        .aspectRatio(aspectRatio)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Image(
                        painter = state.painter,
                        contentDescription = "Post image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        contentScale = ContentScale.Crop
                    )
                }
            },
            error = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading image")
                }
            }
        )
    }
}

