package com.rmp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rmp.data.baseUrl
import com.rmp.data.repository.forum.PostDto

@Composable
fun PostList(
    posts: List<PostDto>,
    upvoted: (Long, Boolean) -> Unit,
    authorClicked: (Long) -> Unit,
) {
    for (post in posts) {
        Row {
            Column {
                Text("Author: ${post.authorNickname}", modifier = Modifier.clickable { authorClicked(post.authorId) })
                Text(post.title)
                Text(post.text ?: "")
                if (post.image != null)
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = "$baseUrl/files/${post.image}"
                        ),
                        contentDescription = "Post image"
                    )
                OutlinedButton(onClick = {
                    upvoted(post.id, !post.upVoted)
                }) { Text( if (post.upVoted) "Downvote" else "Upvote") }
                Text("Upvotes: ${post.upVotes}")
            }
        }
        HorizontalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 15.dp))
    }
}
