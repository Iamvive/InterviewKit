package com.interview.kit.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.interview.kit.ui.home.HomeScreen
import androidx.compose.ui.tooling.preview.Preview
import com.interview.kit.ui.theme.InterviewKitTheme
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
data class PostDetailRoute(val postId: Int)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
            
            ListDetailPaneScaffold(
                directive = navigator.scaffoldDirective,
                value = navigator.scaffoldValue,
                listPane = {
                    AnimatedPane {
                        HomeScreen(
                            viewModel = hiltViewModel(),
                            onPostClick = { post ->
                                navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, post.id)
                            }
                        )
                    }
                },
                detailPane = {
                    AnimatedPane {
                        val postId = navigator.currentDestination?.content
                        if (postId != null) {
                            PostDetailContent(postId)
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Select a post to see details")
                            }
                        }
                    }
                }
            )
            
            BackHandler(navigator.canNavigateBack()) {
                navigator.navigateBack()
            }
        }
    }
}

@Composable
fun PostDetailContent(postId: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Post Detail: $postId")
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun AppNavGraphPreview() {
    InterviewKitTheme {
        AppNavGraph()
    }
}
