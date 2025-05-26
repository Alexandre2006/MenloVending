package dev.thinkalex.menlovending.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenloVendingSuccess(
    onCancel: () -> Unit = { },
) {

    // Display
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Enjoy your purchase!",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        // Cancel Button (filled, red)
        Text(
            text = "Back to Menu",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable { onCancel() }
                .padding(8.dp),
            color = MaterialTheme.colorScheme.error
        )
    }
}