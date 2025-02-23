package dev.thinkalex.menlovending.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenloVendingScaffold(title: String = "Menlo Vending", subtitle: String = "By Cody Kletter", content: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (subtitle.isNotEmpty()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(title, style = MaterialTheme.typography.titleLarge)
                            Text(subtitle, style = MaterialTheme.typography.titleSmall)
                        }
                    } else {
                        Text(title, style = MaterialTheme.typography.headlineMedium)
                    }
                }
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.add(WindowInsets(16, 16, 16, 16))
    ) { innerPadding -> content(innerPadding) }
}