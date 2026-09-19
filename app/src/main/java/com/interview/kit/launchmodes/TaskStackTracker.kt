package com.interview.kit.launchmodes

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ActivityNode(
    val id: String,
    val name: String,
    val launchMode: String,
    val taskId: Int,
    val hashCodeHex: String,
    val state: String,
    val timestamp: String
)

data class IntentLog(
    val targetActivity: String,
    val message: String,
    val flags: List<String>,
    val timestamp: String
)

object TaskStackTracker : Application.ActivityLifecycleCallbacks {

    private val _stackFlow = MutableStateFlow<List<ActivityNode>>(emptyList())
    val stackFlow: StateFlow<List<ActivityNode>> = _stackFlow.asStateFlow()

    private val _logsFlow = MutableStateFlow<List<IntentLog>>(emptyList())
    val logsFlow: StateFlow<List<IntentLog>> = _logsFlow.asStateFlow()

    private val activeActivities = mutableListOf<ActivityNode>()
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(this)
    }

    fun logNewIntent(activityName: String, message: String, flags: List<String> = emptyList()) {
        val newLog = IntentLog(
            targetActivity = activityName,
            message = message,
            flags = flags,
            timestamp = timeFormat.format(Date())
        )
        _logsFlow.value = listOf(newLog) + _logsFlow.value.take(20)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        val launchModeName = getLaunchModeName(activity)
        val node = ActivityNode(
            id = "${activity::class.java.simpleName}@${Integer.toHexString(activity.hashCode())}",
            name = activity::class.java.simpleName,
            launchMode = launchModeName,
            taskId = activity.taskId,
            hashCodeHex = "@${Integer.toHexString(activity.hashCode())}",
            state = "Created",
            timestamp = timeFormat.format(Date())
        )
        activeActivities.add(node)
        _stackFlow.value = activeActivities.toList()
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        val hex = "@${Integer.toHexString(activity.hashCode())}"
        val index = activeActivities.indexOfLast { it.hashCodeHex == hex }
        if (index != -1) {
            val updated = activeActivities[index].copy(state = "Resumed")
            activeActivities[index] = updated
            _stackFlow.value = activeActivities.toList()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        val hex = "@${Integer.toHexString(activity.hashCode())}"
        val index = activeActivities.indexOfLast { it.hashCodeHex == hex }
        if (index != -1) {
            val updated = activeActivities[index].copy(state = "Paused")
            activeActivities[index] = updated
            _stackFlow.value = activeActivities.toList()
        }
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        val hex = "@${Integer.toHexString(activity.hashCode())}"
        activeActivities.removeAll { it.hashCodeHex == hex }
        _stackFlow.value = activeActivities.toList()
    }

    private fun getLaunchModeName(activity: Activity): String {
        return when (activity::class.java.simpleName) {
            "ActivityA" -> "standard"
            "ActivityB" -> "singleTop"
            "ActivityC" -> "singleTask"
            "ActivityD" -> "singleInstance"
            else -> "standard"
        }
    }
}
