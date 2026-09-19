package com.interview.kit.launchmodes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.interview.kit.ui.theme.InterviewKitTheme

class ActivityC : ComponentActivity() {

    private var newIntentCount by mutableIntStateOf(0)
    private var lastIntentMessage by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lastIntentMessage = intent.getStringExtra("EXTRA_MESSAGE")

        setContent {
            InterviewKitTheme {
                LaunchModePlaygroundUi(
                    currentActivityName = "Activity C",
                    launchMode = "singleTask",
                    themeColor = Color(0xFFFF9800),
                    newIntentCount = newIntentCount,
                    lastIntentMessage = lastIntentMessage,
                    onFinishActivity = { finish() }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        newIntentCount++
        lastIntentMessage = intent.getStringExtra("EXTRA_MESSAGE")
    }
}
