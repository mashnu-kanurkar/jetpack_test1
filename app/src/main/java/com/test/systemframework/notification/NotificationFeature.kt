package com.test.systemframework.notification

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationFeatureScreen(viewModel: NotificationViewModel){
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult() ){
        viewModel.refreshNotificationPermission()
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text =
            "Why we need this permission?",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text =
            "To support advanced features of this app, our companion app will send notifications to your device and to read these notifications we need access to notifications manager. While enabling this permission, an additional informative dialog may appear again to specify the notification permission.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            showBottomSheet = true
            //launcher.launch(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }) { Text("Grant Access") }
    }
    if (showBottomSheet){
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            // Sheet content
            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom) {
                Text(text = "This will allow Little nest app to read the necessary notification messages from the companion apps to communicate with companion apps and provide more personalised content for this app",
                    textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                            launcher.launch(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                        }
                    }
                }) {
                    Text("Grant permission")
                }
                Spacer(modifier = Modifier.height(48.dp))

            }

        }
    }
}
