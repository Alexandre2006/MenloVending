package dev.thinkalex.menlovending

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.thinkalex.menlovending.services.permissions.PermissionStatus
import dev.thinkalex.menlovending.services.permissions.checkPermissions
import dev.thinkalex.menlovending.ui.theme.MenloVendingTheme
import dev.thinkalex.menlovending.ui.views.MainView
import dev.thinkalex.menlovending.ui.views.PermissionCheckView

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MenloVendingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    SetupNavGraph(navController, this)
                }
            }
        }
    }
}

// Navigation
@Composable
fun DisableBackHandler(disabled: Boolean = true, content: @Composable () -> Unit) {
    BackHandler(enabled = disabled) {}
    content()
}

@Composable
fun SetupNavGraph(navController: NavHostController, context: Context) {
    // Check for permissions
    LaunchedEffect(Unit) {
        if (checkPermissions(context).any { it.status != PermissionStatus.GRANTED }) {
            navController.navigate("permissionCheck")
        }
    }

    NavHost(
        navController = navController,
        startDestination = "mainView"
    ) {
        composable("permissionCheck") {
            DisableBackHandler {
                PermissionCheckView(navController = navController)
            }
        }
        composable("mainView") {
            MainView()
        }
    }
}