package dev.thinkalex.menlovending.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contactless
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.thinkalex.menlovending.services.item.ItemDetailsResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenloVendingPurchase(
    product: ItemDetailsResult?,
    status: String,
    onCancel: () -> Unit = { },
    cancellable: Boolean = true
) {

    // Display
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        // Product Name / Price
        Text(
            text = product?.name ?: "Unknown Product",
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = "$${product?.price?.let { String.format("%.2f", it) } ?: "N/A"}",
            style = MaterialTheme.typography.headlineLarge,
        )

        // ContactLess Logo
        Icon(
            imageVector = Icons.Default.Contactless,
            contentDescription = "Contactless Payment",
            modifier = Modifier.size(192.dp).padding(top = 16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Status
        Text(
            text = status,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Cancel Button (filled, red)
        Text(
            text = "Cancel Purchase",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(top = 16.dp)
                .clickable(enabled = cancellable) { if (cancellable) onCancel() }
                .padding(8.dp),
            color = if (cancellable) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )

    }
}