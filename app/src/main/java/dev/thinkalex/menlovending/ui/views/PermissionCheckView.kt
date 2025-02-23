package dev.thinkalex.menlovending.ui.views

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import dev.thinkalex.menlovending.services.permissions.Permission
import dev.thinkalex.menlovending.services.permissions.PermissionStatus
import dev.thinkalex.menlovending.services.permissions.checkPermissions
import dev.thinkalex.menlovending.ui.widgets.MenloVendingScaffold

@Composable
fun PermissionCheckView(modifier: Modifier = Modifier) {
    // Context & Activity
    val context = LocalContext.current
    val activity = LocalActivity.current

    // Permission List
    var permissionList by remember { mutableStateOf(checkPermissions(context)) }

    // Permission Launcher
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { _ ->
            // After requesting, update the permission list.
            permissionList = checkPermissions(context)
        }

    // Observer for Permission Changes (when user changes permission from settings)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionList = checkPermissions(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Request Permission
    fun onRequest(permission: Permission) {
        permissionLauncher.launch(permission.name)
    }

    fun onOpenSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    // View
    MenloVendingScaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Missing Required Permissions", style = MaterialTheme.typography.titleLarge)
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp),
                ) {
                    items(permissionList, key = { it.name }) { permission ->
                        PermissionCheckRow(
                            permission = permission,
                            onRequest = { onRequest(permission) },
                            onOpenSettings = { onOpenSettings() }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

}

@Composable
fun PermissionCheckRow(
    permission: Permission,
    onRequest: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = permission.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                when (permission.status) {
                    PermissionStatus.GRANTED -> {
                        Text(
                            text = "Status: Granted",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    PermissionStatus.DENIED -> {
                        Text(
                            text = "Status: Denied",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    PermissionStatus.BLOCKED -> {
                        Text(
                            text = "Status: Blocked",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            when (permission.status) {
                PermissionStatus.GRANTED -> {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Permission Granted",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                PermissionStatus.DENIED -> {
                    Button(onClick = onRequest) {
                        Text(text = "Request")
                    }
                }

                PermissionStatus.BLOCKED -> {
                    Button(onClick = onOpenSettings) {
                        Text(text = "Open Settings")
                    }
                }
            }
        }
    }
}