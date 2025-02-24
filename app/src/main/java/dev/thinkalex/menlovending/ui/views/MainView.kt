package dev.thinkalex.menlovending.ui.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.thinkalex.menlovending.ui.widgets.MenloVendingStatusScaffold

@Composable
fun MainView(modifier: Modifier = Modifier) {
    MenloVendingStatusScaffold {
        Text("HI!")
    }
}