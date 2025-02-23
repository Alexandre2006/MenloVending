package dev.thinkalex.menlovending.services.permissions

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

// Permissions (API Level 30 and below)
private val permissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN
)

private val permissionsTitle = mapOf(
    Manifest.permission.ACCESS_FINE_LOCATION to "Fine Location",
    Manifest.permission.BLUETOOTH to "Bluetooth",
    Manifest.permission.BLUETOOTH_ADMIN to "Bluetooth Admin"
)

// Permissions (API Level 31 and above)
@RequiresApi(Build.VERSION_CODES.S)
private val permissionsS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
)

@RequiresApi(Build.VERSION_CODES.S)
private val permissionsSTitle = mapOf(
    Manifest.permission.ACCESS_FINE_LOCATION to "Fine Location",
    Manifest.permission.BLUETOOTH_SCAN to "Bluetooth Scan",
    Manifest.permission.BLUETOOTH_CONNECT to "Bluetooth Connect"
)

// Permission Status
enum class PermissionStatus {
    GRANTED,    // Permission Granted
    DENIED,     // Permission Denied (Can be requested)
    BLOCKED     // Permission Denied (Cannot be requested, must open settings)
}

// Permission Class
data class Permission(val name: String, val title: String, val status: PermissionStatus)

// Permission Checking Function
fun checkPermissions(context: Context): List<Permission> {
    val permissionList = mutableListOf<Permission>()
    val currentPermissions =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) permissions else permissionsS
    val currentPermissionsTitle =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) permissionsTitle else permissionsSTitle

    currentPermissions.forEach {
        val status = when {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> PermissionStatus.GRANTED

            androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale(
                context as android.app.Activity,
                it
            ) -> PermissionStatus.BLOCKED

            else -> PermissionStatus.DENIED
        }
        permissionList.add(Permission(it, currentPermissionsTitle[it]!!, status))
    }

    return permissionList
}