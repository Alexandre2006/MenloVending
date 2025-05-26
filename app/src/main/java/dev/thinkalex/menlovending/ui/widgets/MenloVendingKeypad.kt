package dev.thinkalex.menlovending.ui.widgets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenloVendingKeypad(
    onConfirm: (String) -> Unit = {}
) {
    var value by remember { mutableStateOf("") }
    val buttonColor = Color(0xFFCCCCCC) // Darker gray
    val textColor = Color.Black

    fun addDigit(digit: Int) {
        if (value.length < 2) {
            value += digit.toString()
        }
    }

    // Display
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Enter item number:",
            style = MaterialTheme.typography.headlineSmall,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp), // Adjust height as needed
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontSize = 32.sp,
                color = textColor,
                maxLines = 1
            )
        }

        // Keypad Buttons (1-9 / 3x3 grid)
        for (row in 0..2) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                for (col in 1..3) {
                    val digit = row * 3 + col
                    Button(
                        onClick = {
                            addDigit(digit)
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = textColor
                        )
                    ) {
                        Text(
                            text = digit.toString(),
                            fontSize = 24.sp,
                            color = textColor
                        )
                    }
                }
            }
        }

        // Keypad Buttons (Backspace, 0, Confirm)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Button(
                onClick = {
                    if (value.isNotEmpty()) value = value.dropLast(1)
                },
                shape = CircleShape,
                modifier = Modifier.size(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = textColor
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Default.Backspace,
                    contentDescription = "Backspace",
                    modifier = Modifier.size(24.dp),
                    tint = textColor
                )
            }
            Button(
                onClick = {
                    addDigit(0)
                },
                shape = CircleShape,
                modifier = Modifier.size(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = textColor
                ),
            ) {
                Text("0", fontSize = 24.sp, color = textColor)
            }
            Button(
                onClick = {
                    onConfirm(value)
                },
                shape = CircleShape,
                modifier = Modifier.size(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = textColor,
                ),
                contentPadding = PaddingValues(0.dp),
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Confirm",
                    modifier = Modifier.size(24.dp),
                    tint = textColor,
                    )
            }
        }
    }
}