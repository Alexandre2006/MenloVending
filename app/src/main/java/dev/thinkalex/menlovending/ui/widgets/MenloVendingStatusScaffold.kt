package dev.thinkalex.menlovending.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import dev.thinkalex.menlovending.services.manager.MenloVendingManager
import dev.thinkalex.menlovending.services.manager.MenloVendingState

// State ColorMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenloVendingStatusScaffold( content: @Composable (PaddingValues) -> Unit) {
    // Status
    val status by MenloVendingManager.status.collectAsState()

    // Color / Icon Mapping
    val statusColorMap = mapOf(
        MenloVendingState.MenloVendingStatus.INITIALIZING to colorScheme.inversePrimary,
        MenloVendingState.MenloVendingStatus.READY to colorScheme.inversePrimary,
        MenloVendingState.MenloVendingStatus.WARNING to colorScheme.errorContainer,
        MenloVendingState.MenloVendingStatus.ERROR to colorScheme.errorContainer,
        MenloVendingState.MenloVendingStatus.FATAL to colorScheme.error,
    )

    val statusIconMap = mapOf(
        MenloVendingState.MenloVendingStatus.INITIALIZING to Icons.Default.Info,
        MenloVendingState.MenloVendingStatus.READY to Icons.Default.CheckCircle,
        MenloVendingState.MenloVendingStatus.WARNING to Icons.Default.WarningAmber,
        MenloVendingState.MenloVendingStatus.ERROR to Icons.Default.ErrorOutline,
        MenloVendingState.MenloVendingStatus.FATAL to Icons.Default.Error,
    )

    if (status.status == MenloVendingState.MenloVendingStatus.READY) {
        // Show Loading
        MenloVendingScaffold { innerPadding -> content(innerPadding)}
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Menlo Vending",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.Black
                            )
                            Text(
                                text = status.statusMessage,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Black
                            )
                        }
                    },
                    actions = {
                        Icon(
                            imageVector = statusIconMap[status.status] ?: Icons.Default.Clear,
                            contentDescription = "Status Icon",
                            tint = Color.Black
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                        containerColor = statusColorMap[status.status] ?: colorScheme.onBackground,
                    )
                )
            },
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets.add(WindowInsets(16, 16, 16, 16))
        ) {  innerPadding -> content(innerPadding)}

    }
}