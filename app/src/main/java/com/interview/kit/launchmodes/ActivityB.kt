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

data class SetIntentDemoState(
    val callSetIntentEnabled: Boolean,
    val onToggleSetIntent: (Boolean) -> Unit,
    val receivedOnNewIntent: String?,
    val currentGetIntentResult: String?,
    val onSendSelfIntent: (String) -> Unit,
    val onReadGetIntent: () -> Unit
)

class ActivityB : ComponentActivity() {

    private var newIntentCount by mutableIntStateOf(0)
    private var lastReceivedOnNewIntentPayload by mutableStateOf<String?>(null)
    private var callSetIntentEnabled by mutableStateOf(false) // Toggle to simulate the bug!
    private var currentGetIntentPayload by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        currentGetIntentPayload = intent.getStringExtra("EXTRA_MESSAGE") ?: "Initial Intent #1 (at onCreate)"

        setContent {
            InterviewKitTheme {
                LaunchModePlaygroundUi(
                    currentActivityName = "Activity B",
                    launchMode = "singleTop",
                    themeColor = Color(0xFF009688),
                    newIntentCount = newIntentCount,
                    lastIntentMessage = lastReceivedOnNewIntentPayload,
                    onFinishActivity = { finish() },
                    setIntentDemo = SetIntentDemoState(
                        callSetIntentEnabled = callSetIntentEnabled,
                        onToggleSetIntent = { callSetIntentEnabled = it },
                        receivedOnNewIntent = lastReceivedOnNewIntentPayload,
                        currentGetIntentResult = currentGetIntentPayload,
                        onSendSelfIntent = { message ->
                            val intent = Intent(this, ActivityB::class.java).apply {
                                putExtra("EXTRA_MESSAGE", message)
                            }
                            startActivity(intent)
                        },
                        onReadGetIntent = {
                            currentGetIntentPayload = intent.getStringExtra("EXTRA_MESSAGE")
                        }
                    )
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val payload = intent.getStringExtra("EXTRA_MESSAGE")
        lastReceivedOnNewIntentPayload = payload
        newIntentCount++

        if (callSetIntentEnabled) {
            setIntent(intent) // Correct way: updates getIntent()
        } else {
            // ⚠️ Simulated interview bug: setIntent(intent) skipped!
        }

        currentGetIntentPayload = getIntent().getStringExtra("EXTRA_MESSAGE")
    }
}
