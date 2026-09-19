package com.interview.kit.launchmodes

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LaunchModePlaygroundUi(
    currentActivityName: String,
    launchMode: String,
    themeColor: Color,
    newIntentCount: Int,
    lastIntentMessage: String?,
    onFinishActivity: () -> Unit
) {
    val context = LocalContext.current
    val currentActivity = context as? Activity
    val currentTaskId = currentActivity?.taskId ?: 0
    val currentHashCode = currentActivity?.let { "@${Integer.toHexString(it.hashCode())}" } ?: ""

    val stackNodes by TaskStackTracker.stackFlow.collectAsStateWithLifecycle()
    val logs by TaskStackTracker.logsFlow.collectAsStateWithLifecycle()

    var flagNewTask by remember { mutableStateOf(false) }
    var flagSingleTop by remember { mutableStateOf(false) }
    var flagClearTop by remember { mutableStateOf(false) }
    var flagClearTask by remember { mutableStateOf(false) }
    var flagReorderToFront by remember { mutableStateOf(false) }

    fun buildIntent(targetClass: Class<out Activity>): Intent {
        val intent = Intent(context, targetClass)
        val selectedFlags = mutableListOf<String>()

        if (flagNewTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            selectedFlags.add("NEW_TASK")
        }
        if (flagSingleTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            selectedFlags.add("SINGLE_TOP")
        }
        if (flagClearTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            selectedFlags.add("CLEAR_TOP")
        }
        if (flagClearTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            selectedFlags.add("CLEAR_TASK")
        }
        if (flagReorderToFront) {
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            selectedFlags.add("REORDER_TO_FRONT")
        }

        intent.putExtra("EXTRA_MESSAGE", "Sent from $currentActivityName at ${System.currentTimeMillis() % 100000}")
        TaskStackTracker.logNewIntent(targetClass.simpleName, "Launched from $currentActivityName", selectedFlags)
        return intent
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = themeColor,
                            modifier = Modifier.size(16.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = currentActivityName,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = themeColor.copy(alpha = 0.15f),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text(
                            text = "launchMode: $launchMode",
                            color = themeColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Info Card
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = themeColor.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Task ID", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text("$currentTaskId", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Instance HashCode", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(currentHashCode, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }

                        if (newIntentCount > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🔄 onNewIntent called $newIntentCount time(s)! Last: ${lastIntentMessage ?: "none"}",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Task & Back Stack Visualizer
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Layers, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live Tasks & Back Stack (Bottom → Top)",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Group stack by TaskId
                        val tasks = stackNodes.groupBy { it.taskId }
                        if (tasks.isEmpty()) {
                            Text("Stack empty", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        } else {
                            tasks.forEach { (taskId, nodes) ->
                                Text(
                                    text = "Task #$taskId ${if (taskId == currentTaskId) "(Current Task)" else "(Background Task)"}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (taskId == currentTaskId) MaterialTheme.colorScheme.primary else Color.Gray,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(10.dp))
                                        .padding(8.dp)
                                ) {
                                    nodes.forEachIndexed { idx, node ->
                                        val isCurrent = node.hashCodeHex == currentHashCode
                                        val nodeColor = when (node.name) {
                                            "ActivityA" -> Color(0xFF3F51B5)
                                            "ActivityB" -> Color(0xFF009688)
                                            "ActivityC" -> Color(0xFFFF9800)
                                            "ActivityD" -> Color(0xFF9C27B0)
                                            else -> Color.Gray
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(
                                                    width = if (isCurrent) 2.dp else 1.dp,
                                                    color = if (isCurrent) nodeColor else Color.LightGray.copy(alpha = 0.5f),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .background(
                                                    if (isCurrent) nodeColor.copy(alpha = 0.12f) else Color.Transparent,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("[${idx + 1}]", color = Color.Gray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(node.name, fontWeight = FontWeight.Bold, color = nodeColor)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(node.hashCodeHex, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.Gray)
                                            }

                                            Surface(
                                                color = if (isCurrent) nodeColor else Color.Gray.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = if (isCurrent) "ACTIVE TOP" else node.state,
                                                    color = if (isCurrent) Color.White else Color.DarkGray,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            // Intent Flags Selector
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Attach Intent Flags (Optional)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlagCheckbox(label = "FLAG_ACTIVITY_NEW_TASK", checked = flagNewTask, onCheckedChange = { flagNewTask = it })
                        FlagCheckbox(label = "FLAG_ACTIVITY_SINGLE_TOP", checked = flagSingleTop, onCheckedChange = { flagSingleTop = it })
                        FlagCheckbox(label = "FLAG_ACTIVITY_CLEAR_TOP", checked = flagClearTop, onCheckedChange = { flagClearTop = it })
                        FlagCheckbox(label = "FLAG_ACTIVITY_CLEAR_TASK", checked = flagClearTask, onCheckedChange = { flagClearTask = it })
                        FlagCheckbox(label = "FLAG_ACTIVITY_REORDER_TO_FRONT", checked = flagReorderToFront, onCheckedChange = { flagReorderToFront = it })
                    }
                }
            }

            // Launch Target Buttons
            item {
                Text("Launch Next Activity:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LaunchButton(
                        name = "Activity A",
                        mode = "standard",
                        color = Color(0xFF3F51B5),
                        onClick = { context.startActivity(buildIntent(ActivityA::class.java)) }
                    )
                    LaunchButton(
                        name = "Activity B",
                        mode = "singleTop",
                        color = Color(0xFF009688),
                        onClick = { context.startActivity(buildIntent(ActivityB::class.java)) }
                    )
                    LaunchButton(
                        name = "Activity C",
                        mode = "singleTask",
                        color = Color(0xFFFF9800),
                        onClick = { context.startActivity(buildIntent(ActivityC::class.java)) }
                    )
                    LaunchButton(
                        name = "Activity D",
                        mode = "singleInstance",
                        color = Color(0xFF9C27B0),
                        onClick = { context.startActivity(buildIntent(ActivityD::class.java)) }
                    )
                }
            }

            // Finish Activity Button
            item {
                OutlinedButton(
                    onClick = onFinishActivity,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Finish Current Activity (Pop Back Stack)")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun FlagCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun LaunchButton(
    name: String,
    mode: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Launch $name", fontWeight = FontWeight.Bold)
            Text("[$mode]", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}
