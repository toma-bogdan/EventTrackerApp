package com.example.eventtrackerkotlincompose.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.eventtrackerkotlincompose.config.AppConfig
import com.example.eventtrackerkotlincompose.network.Role
import com.example.eventtrackerkotlincompose.network.User
import com.example.eventtrackerkotlincompose.network.UserComment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun EventCommentsSection(
    comments: List<UserComment>,
    onCommentSubmit: (String) -> Unit,
    user: User,
) {
    var newCommentText by remember { mutableStateOf("") }
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(
            text = "Comments",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = Color.Black)
        CommentInputSection(
            newCommentText = newCommentText,
            onCommentTextChange = { newCommentText = it },
            user = user,
            onSubmit = {
                onCommentSubmit(newCommentText)
                newCommentText = ""
            }
        )
        comments.forEach { comment ->
            UserCommentItem(comment = comment)
        }
    }
}

@Composable
fun UserCommentItem(comment: UserComment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        if (comment.user.profile_image != null) {
            val correctUrl = comment.user.profile_image!!.replace("localhost", AppConfig.SERVER_IP)
            AsyncImage(
                model = correctUrl,
                contentDescription = "User Profile Image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Default Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${comment.user.firstName} ${comment.user.lastName}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (comment.user.role != Role.USER) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = comment.user.role.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text(
                text = comment.comment,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun CommentInputSection(
    newCommentText: String,
    onCommentTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    user: User
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    )
    {
        Row (verticalAlignment = Alignment.CenterVertically){
            if (user.profile_image != null) {
                val correctUrl = user.profile_image!!.replace("localhost", AppConfig.SERVER_IP)
                AsyncImage(
                    model = correctUrl,
                    contentDescription = "User Profile Image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                )
            }

            TextField(
                value = newCommentText,
                onValueChange = onCommentTextChange,
                label = { Text("Add a comment") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (newCommentText.isNotEmpty()) {
            Button(
                onClick = onSubmit,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Comment")
            }
        }
    }
}