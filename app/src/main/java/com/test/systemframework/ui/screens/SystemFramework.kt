package com.test.systemframework.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.systemframework.viewmodel.SystemFrameworkViewModel

@Composable
fun SystemFrameworkScreen(context: Context, viewModel: SystemFrameworkViewModel = viewModel()) {
    val batteryInfo = viewModel.batteryInfo
    val storageInfo = viewModel.storageInfo
    val networkStatus = viewModel.networkStatus
    val deviceInfo = viewModel.deviceInfo
    val sensorStatus = viewModel.sensorStatus

    LaunchedEffect(Unit) {
        viewModel.loadSystemData(context)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item { InfoSection(title = "Battery Information", content = batteryInfo) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { InfoSection(title = "Storage Usage", content = storageInfo) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { InfoSection(title = "Network Status", content = networkStatus) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { InfoSection(title = "Device Information", content = deviceInfo) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { InfoSection(title = "Sensor Status", content = sensorStatus) }
        }
    }
}

@Composable
fun InfoSection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = content,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun SystemFrameworkPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val systemFrameworkViewModel: SystemFrameworkViewModel = viewModel()
    SystemFrameworkScreen(context, systemFrameworkViewModel)
}
