package com.interview.kit.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import androidx.compose.ui.tooling.preview.Preview
import com.interview.kit.ui.theme.InterviewKitTheme
import com.interview.kit.domain.model.Post

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.interview.kit.ui.ai.AiAssistantSheet
import com.interview.kit.ui.ai.AiViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier,
    aiViewModel: AiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAiSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAiSheet = true },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                text = { Text("AI Copilot") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is HomeUiState.Success -> {
                    PostList(
                        posts = state.posts,
                        onPostClick = onPostClick
                    )
                }
                is HomeUiState.Error -> {
                    ErrorView(
                        message = state.message,
                        onRetry = { viewModel.fetchPosts() }
                    )
                }
            }
        }
    }

    if (showAiSheet) {
        AiAssistantSheet(
            viewModel = aiViewModel,
            onDismiss = { showAiSheet = false }
        )
    }
}

@Composable
fun PostList(
    posts: List<Post>,
    onPostClick: (Post) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(posts) { post ->
            PostItem(
                post = post,
                modifier = Modifier.clickable { onPostClick(post) }
            )
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = "https://picsum.photos/seed/${post.id}/200",
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }
    }
}

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    InterviewKitTheme {
        Box {
            Text("Home Screen Preview")
        }
    }
}
