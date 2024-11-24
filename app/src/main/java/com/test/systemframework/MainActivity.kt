package com.test.systemframework

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import com.test.systemframework.notification.NotificationListener
import com.test.systemframework.remote.NotificationSyncMonitor
import com.test.systemframework.ui.screens.SystemFrameworkScreen
import com.test.systemframework.ui.theme.SystemFrameworkTheme
import com.test.systemframework.viewmodel.SystemFrameworkViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private var isServiceBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isServiceBound = true
            // ...
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
            // ...
        }
    }

    private lateinit var analytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val systemFrameworkViewModel: SystemFrameworkViewModel = ViewModelProvider(this).get(SystemFrameworkViewModel::class)
        analytics = Firebase.analytics
        analytics.logEvent(
            FirebaseAnalytics.Event.APP_OPEN,
        ){
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "app_open")
        }
        enableEdgeToEdge()
        setContent {
            SystemFrameworkTheme {
                SystemFrameworkScreen(
                    context = this,
                    viewModel = systemFrameworkViewModel
                )
            }
        }

        lifecycleScope.launch {
            createWorkRequest()
        }
        val intent = Intent(this, NotificationListener::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

    }

    private suspend fun createWorkRequest(){
        val notificationUploadRequest = PeriodicWorkRequestBuilder<NotificationSyncMonitor>(
            4, TimeUnit.HOURS
        ).setConstraints(
            Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED) // Only run when connected
                    .build())
            .addTag("notificationUpload")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "notificationUpload",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationUploadRequest
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unbind the service if it's bound
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
    }
}
