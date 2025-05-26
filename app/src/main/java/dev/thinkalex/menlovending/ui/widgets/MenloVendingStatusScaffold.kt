package dev.thinkalex.menlovending.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.thinkalex.menlovending.services.manager.MenloVendingManager
import dev.thinkalex.menlovending.services.manager.MenloVendingState


@Composable
fun UnavailableMessage(modifier: Modifier = Modifier) {
    val status by MenloVendingManager.status.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Title Text
        Text(
            text = "Currently\nUnavailable",
            style = MaterialTheme.typography.headlineLarge,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        // Subtitle / Contact Text
        Text(
            "If the issue persists, please contact Cody Kletter.",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onBackground
        )

        // Error Message
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { expanded = !expanded }
                .padding(top = 8.dp)
        ) {
            Text(
                text = if (expanded) "Hide Error Details" else "Show Error Details",
                color = colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = colorScheme.primary
            )
        }
        if (expanded) {
            Text(
                text = status.statusMessage + "\n" + status.statusDetails,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

// State ColorMap
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenloVendingStatusScaffold(content: @Composable (PaddingValues) -> Unit) {
    // Status
    val status by MenloVendingManager.status.collectAsState()

    // Color / Icon Mapping
    val statusColorMap = mapOf(
        MenloVendingState.MenloVendingStatus.INITIALIZING to colorScheme.inversePrimary,
        MenloVendingState.MenloVendingStatus.READY to colorScheme.inversePrimary,
        MenloVendingState.MenloVendingStatus.WARNING to colorScheme.errorContainer,
        MenloVendingState.MenloVendingStatus.ERROR to colorScheme.error,
    )

    val statusIconMap = mapOf(
        MenloVendingState.MenloVendingStatus.INITIALIZING to Icons.Default.Info,
        MenloVendingState.MenloVendingStatus.READY to Icons.Default.CheckCircle,
        MenloVendingState.MenloVendingStatus.WARNING to Icons.Default.WarningAmber,
        MenloVendingState.MenloVendingStatus.ERROR to Icons.Default.Error,
    )

    if (status.status == MenloVendingState.MenloVendingStatus.READY) {
        // Show Loading
        MenloVendingScaffold { innerPadding -> content(innerPadding) }
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
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets.add(
                WindowInsets(
                    16,
                    16,
                    16,
                    16
                )
            ),
        ) { contentPadding ->
            UnavailableMessage(modifier = Modifier.padding(contentPadding))
        }

    }
}